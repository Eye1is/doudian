package me.gaigeshen.doudian.api.authorization;

import me.gaigeshen.doudian.api.AppConfig;
import me.gaigeshen.doudian.api.Constants;
import me.gaigeshen.doudian.api.http.WebClient;
import me.gaigeshen.doudian.api.http.WebClientException;
import me.gaigeshen.doudian.api.request.DefaultResponse;
import me.gaigeshen.doudian.api.request.RequestResultException;
import me.gaigeshen.doudian.api.request.ResponseParseException;
import me.gaigeshen.doudian.api.request.ResponseParser;
import me.gaigeshen.doudian.api.util.URLCodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;

import java.util.Objects;

import static me.gaigeshen.doudian.api.Constants.getAccessTokenUri;

/**
 * 授权流程处理器实现
 *
 * @author gaigeshen
 */
public class AuthorizationProcessorImpl implements AuthorizationProcessor {

  private final WebClient webClient = WebClient.builder().build();

  private final ResponseParser<AccessToken> accessTokenResponseParser = new AccessTokenResponseParserImpl();

  private final AppConfig appConfig;

  private final AccessTokenManager accessTokenManager;

  private final String redirectUri;

  /**
   * 创建授权流程处理器
   *
   * @param appConfig 应用配置
   * @param accessTokenManager 访问令牌管理器
   * @param redirectUri 授权回调地址
   */
  public AuthorizationProcessorImpl(AppConfig appConfig, AccessTokenManager accessTokenManager, String redirectUri) {
    if (Objects.isNull(appConfig)) {
      throw new IllegalArgumentException("appConfig cannot be null");
    }
    if (Objects.isNull(accessTokenManager)) {
      throw new IllegalArgumentException("accessTokenManager cannot be null");
    }
    if (StringUtils.isBlank(redirectUri)) {
      throw new IllegalArgumentException("redirectUri cannot be blank or null");
    }
    this.appConfig = appConfig;
    this.accessTokenManager = accessTokenManager;
    this.redirectUri = URLCodecUtils.encode(redirectUri, "utf-8");
  }

  @Override
  public String getAuthorizeUri(String state) {
    return Constants.getShopAuthorizeUri(appConfig.getAppKey(), redirectUri, StringUtils.defaultString(state));
  }

  @Override
  public String getAuthorizeUri() {
    return getAuthorizeUri("");
  }

  @Override
  public AccessToken handleAuthorized(String authorizationCode, String state) throws AuthorizationProcessorException {
    // 暂时先不管第二个参数
    return handleAuthorized(authorizationCode);
  }

  @Override
  public AccessToken handleAuthorized(String authorizationCode) throws AuthorizationProcessorException {
    AccessToken accessToken;
    try {
      accessToken = getRemoteAccessToken(authorizationCode);
    } catch (Exception e) {
      throw new IllegalStateException("Could not handle authorized:: authorization code " + authorizationCode);
    }
    try {
      return accessTokenManager.saveAccessToken(accessToken);
    } catch (AccessTokenManagerException e) {
      throw new AuthorizationProcessorException("Could not handle authorization code:: " + authorizationCode, e);
    }
  }

  /**
   * 通过授权码获取远程的访问令牌
   *
   * @param authorizationCode 授权码
   * @return 获取远程的访问令牌
   * @throws WebClientException 请求远程数据失败
   * @throws ResponseParseException 转换请求结果失败
   * @throws RequestResultException 请求执行结果异常
   */
  private AccessToken getRemoteAccessToken(String authorizationCode) throws WebClientException, ResponseParseException, RequestResultException {
    HttpGet req = new HttpGet(getAccessTokenUri(appConfig.getAppKey(), appConfig.getAppSecret(), authorizationCode));
    String rawString = webClient.execute(req);
    return accessTokenResponseParser.parse(DefaultResponse.create(rawString));
  }
}
