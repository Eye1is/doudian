package me.gaigeshen.doudian.api.request;

import me.gaigeshen.doudian.api.AppConfig;
import me.gaigeshen.doudian.api.http.WebClient;
import me.gaigeshen.doudian.api.request.param.Params;
import me.gaigeshen.doudian.api.request.sign.SignGeneratorFactory;
import me.gaigeshen.doudian.api.request.sign.SignMethod;
import me.gaigeshen.doudian.api.request.sign.SignParams;
import me.gaigeshen.doudian.api.util.TimestampUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认的请求客户端
 *
 * @author gaigeshen
 */
public class DefaultClient implements Client {

  /** 表示版本号 */
  public final String VERSION = "2";

  /** 本应用配置 */
  private final AppConfig appConfig;

  /** 用于网络请求 */
  private final WebClient webClient;

  public DefaultClient(AppConfig appConfig, WebClient webClient) {
    this.appConfig = appConfig;
    this.webClient = webClient;
  }

  @Override
  public Response execute(Request req) {
    if (req instanceof DefaultRequest) {
      return executeDefaultRequest((DefaultRequest) req);
    }
    // 执行简单请求，不关心请求携带的数据
    return DefaultResponse.create(webClient.execute(new HttpGet(req.getUri())));
  }

  /**
   * 执行默认的请求
   *
   * @param defaultRequest 默认的请求对象
   * @return 响应结果
   */
  public Response executeDefaultRequest(DefaultRequest defaultRequest) {
    HttpPost post = new HttpPost(defaultRequest.getUri());
    Params params = defaultRequest.getParams();
    String timestamp = TimestampUtils.getCurrentTimestamp();

    List<NameValuePair> allParams = new ArrayList<>();
    allParams.add(new BasicNameValuePair("method", params.getMethod()));
    allParams.add(new BasicNameValuePair("app_key", appConfig.getAppKey()));
    allParams.add(new BasicNameValuePair("access_token", ""));
    allParams.add(new BasicNameValuePair("param_json", params.toJsonString()));
    allParams.add(new BasicNameValuePair("timestamp", timestamp));
    allParams.add(new BasicNameValuePair("v", VERSION));
    allParams.add(new BasicNameValuePair("sign", generateSign(params, timestamp)));

    post.setEntity(new StringEntity(URLEncodedUtils.format(allParams, "utf-8"), "utf-8"));
    return DefaultResponse.create(webClient.execute(post));
  }

  /**
   * 生成签名
   *
   * @param params 业务参数
   * @param timestamp 时间字符串
   * @return 签名
   */
  private String generateSign(Params params, String timestamp) {
    SignParams signParams = SignParams.builder()
            .appKey(appConfig.getAppKey()).appSecret(appConfig.getAppSecret())
            .timestamp(timestamp).version(VERSION)
            .params(params)
            .build();
    return SignGeneratorFactory.getGenerator(SignMethod.MD5).generate(signParams);
  }
}
