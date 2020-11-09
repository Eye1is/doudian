package me.gaigeshen.doudian.api.request;

import me.gaigeshen.doudian.api.AppConfig;
import me.gaigeshen.doudian.api.authorization.AccessToken;
import me.gaigeshen.doudian.api.authorization.AccessTokenManager;
import me.gaigeshen.doudian.api.http.WebClient;
import me.gaigeshen.doudian.api.request.exception.*;
import me.gaigeshen.doudian.api.request.sign.SignGenerator;
import me.gaigeshen.doudian.api.request.sign.SignGeneratorFactory;
import me.gaigeshen.doudian.api.request.sign.SignMethod;
import me.gaigeshen.doudian.api.request.sign.SignParams;
import me.gaigeshen.doudian.api.util.TimestampUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 默认的请求客户端
 *
 * @author gaigeshen
 */
public class DefaultClient implements Client {

  private final ParamsJsonSerializer paramsJsonSerializer = new ParamsJsonSerializerImpl();

  private final ResultJsonDeserializer resultJsonDeserializer = new ResultJsonDeserializerImpl();

  private final SignMethod signMethod = SignMethod.MD5;

  private final SignGenerator signGenerator = SignGeneratorFactory.getGenerator(signMethod);

  private final AppConfig appConfig;

  private final AccessTokenManager accessTokenManager;

  private final WebClient webClient;

  private DefaultClient(AppConfig appConfig, AccessTokenManager accessTokenManager, WebClient webClient) {
    Validate.isTrue(Objects.nonNull(appConfig), "appConfig is required");
    Validate.isTrue(Objects.nonNull(accessTokenManager), "accessTokenManager is required");
    Validate.isTrue(Objects.nonNull(webClient), "webClient is required");
    this.accessTokenManager = accessTokenManager;
    this.appConfig = appConfig;
    this.webClient = webClient;
  }

  public static DefaultClient create(AppConfig appConfig, AccessTokenManager accessTokenManager, WebClient webClient) {
    return new DefaultClient(appConfig, accessTokenManager, webClient);
  }

  @Override
  public Response execute(Request req) throws RequestException, ResponseCreationException {
    if (req instanceof DefaultRequest) {
      return executeDefaultRequest((DefaultRequest) req);
    }
    return DefaultResponse.create(webClient.execute(new HttpGet(req.getUri())));
  }

  @Override
  public <T extends Result> T executeResult(Request req, Class<T> resultClass) throws RequestException, ResponseCreationException {
    return resultJsonDeserializer.deserializeResult(executeSuccessResultRawString(req), resultClass);
  }

  @Override
  public <T extends Result> List<T> executeResults(Request req, Class<T> resultClass) throws RequestException, ResponseCreationException {
    return resultJsonDeserializer.deserializeResults(executeSuccessResultRawString(req), resultClass);
  }

  private String executeSuccessResultRawString(Request req) throws RequestException, ResponseCreationException {
    Response resp = execute(req);
    if (resp.isFailed()) {
      String message = StringUtils.isNotBlank(resp.getMessage()) ? resp.getMessage() : "Execute response failed:: " + resp.getRawString();
      if (req instanceof SimpleRequest) {
        throw new RequestException(message);
      } else {

      }
    }
    return resp.getResultRawString();
  }

  private Response executeDefaultRequest(DefaultRequest defaultRequest) throws NoAccessTokenRequestException, ResponseCreationException {
    String accessTokenValue = findAccessTokenValue(defaultRequest.getShopId());
    String timestamp = TimestampUtils.getCurrentTimestamp();
    Params params = defaultRequest.getParams();
    String version = "2";

    SignParams signParams = SignParams.builder()
            .appKey(appConfig.getAppKey()).appSecret(appConfig.getAppSecret())
            .timestamp(timestamp).version(version)
            .params(paramsJsonSerializer.serializer(params, true))
            .build();

    List<NameValuePair> allParams = new ArrayList<>();
    allParams.add(new BasicNameValuePair("method", params.getMethod()));
    allParams.add(new BasicNameValuePair("app_key", appConfig.getAppKey()));
    allParams.add(new BasicNameValuePair("access_token", accessTokenValue));
    allParams.add(new BasicNameValuePair("param_json", paramsJsonSerializer.serializer(params)));
    allParams.add(new BasicNameValuePair("timestamp", timestamp));
    allParams.add(new BasicNameValuePair("v", version));
    allParams.add(new BasicNameValuePair("sign", signGenerator.generate(signParams)));

    HttpPost post = new HttpPost(defaultRequest.getUri());
    post.setEntity(new StringEntity(URLEncodedUtils.format(allParams, "utf-8"), "utf-8"));
    return DefaultResponse.create(webClient.execute(post));
  }

  private String findAccessTokenValue(String shopId) throws NoAccessTokenRequestException {
    AccessToken accessToken = accessTokenManager.findAccessToken(shopId);
    if (Objects.isNull(accessToken)) {
      throw new NoAccessTokenRequestException(shopId, "Could not find access token:: shop id " + shopId);
    }
    return accessToken.getAccessToken();
  }
}
