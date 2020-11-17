package me.gaigeshen.doudian.api.request;

import me.gaigeshen.doudian.api.AppConfig;
import me.gaigeshen.doudian.api.authorization.AccessToken;
import me.gaigeshen.doudian.api.authorization.AccessTokenManager;
import me.gaigeshen.doudian.api.http.WebClient;
import me.gaigeshen.doudian.api.http.WebClientException;
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
    Validate.isTrue(Objects.nonNull(appConfig), "appConfig");
    Validate.isTrue(Objects.nonNull(accessTokenManager), "accessTokenManager");
    Validate.isTrue(Objects.nonNull(webClient), "webClient");
    this.accessTokenManager = accessTokenManager;
    this.appConfig = appConfig;
    this.webClient = webClient;
  }

  public static DefaultClient create(AppConfig appConfig, AccessTokenManager accessTokenManager, WebClient webClient) {
    return new DefaultClient(appConfig, accessTokenManager, webClient);
  }

  @Override
  public Response execute(Request req) throws RequestExecutionException {
    if (req instanceof DefaultRequest) {
      try {
        return executeDefaultRequest((DefaultRequest) req);
      } catch (WebClientException e) {
        throw new RequestExecutionException("Web client exception:: uri " + req.getUri(), e);
      }
    }
    String rawString;
    try {
      rawString = webClient.execute(new HttpGet(req.getUri()));
    } catch (WebClientException e) {
      throw new RequestExecutionException("Web client exception:: uri " + req.getUri(), e);
    }
    return DefaultResponse.create(rawString);
  }

  @Override
  public <T extends Result> T executeResult(Request req, Class<T> resultClass) throws RequestExecutionException {
    return resultJsonDeserializer.deserializeResult(executeSuccessResultRawString(req), resultClass);
  }

  @Override
  public <T extends Result> List<T> executeResults(Request req, Class<T> resultClass) throws RequestExecutionException {
    return resultJsonDeserializer.deserializeResults(executeSuccessResultRawString(req), resultClass);
  }

  private String executeSuccessResultRawString(Request req) throws RequestExecutionException {
    Response resp = execute(req);
    if (resp.isFailed()) {
      throw new RequestResultException(StringUtils.isNotBlank(resp.getMessage()) ? resp.getMessage() : resp.getRawString());
    }
    return resp.getResultRawString();
  }

  private Response executeDefaultRequest(DefaultRequest defaultRequest) throws RequestExecutionException, WebClientException {
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

  private String findAccessTokenValue(String shopId) throws RequestExecutionException {
    AccessToken accessToken = null;
    try {
      accessToken = accessTokenManager.findAccessToken(shopId);
    } catch (me.gaigeshen.doudian.api.authorization.AccessTokenManagerException e) {
      e.printStackTrace();
    }
    if (Objects.isNull(accessToken)) {
      throw new RequestExecutionException("Could not find access token:: shop id " + shopId);
    }
    return accessToken.getAccessToken();
  }
}
