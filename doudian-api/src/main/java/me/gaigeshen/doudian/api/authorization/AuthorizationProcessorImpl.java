package me.gaigeshen.doudian.api.authorization;

import me.gaigeshen.doudian.api.AppConfig;
import me.gaigeshen.doudian.api.Constants;
import me.gaigeshen.doudian.api.http.WebClient;
import me.gaigeshen.doudian.api.http.WebClientException;
import me.gaigeshen.doudian.api.request.DefaultResponse;
import me.gaigeshen.doudian.api.request.ResponseParseException;
import me.gaigeshen.doudian.api.util.URLCodecUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.client.methods.HttpGet;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static me.gaigeshen.doudian.api.Constants.getAccessTokenUri;

/**
 * @author gaigeshen
 */
public class AuthorizationProcessorImpl implements AuthorizationProcessor {

  private final WebClient webClient = WebClient.builder().build();

  private final AppConfig appConfig;

  private final AccessTokenManager accessTokenManager;

  private final String redirectUri;

  private AuthorizationProcessorImpl(AppConfig appConfig, AccessTokenManager accessTokenManager, String redirectUri) {
    Validate.isTrue(Objects.nonNull(appConfig), "appConfig cannot be null");
    Validate.isTrue(Objects.nonNull(accessTokenManager), "accessTokenManager cannot be null");
    Validate.isTrue(StringUtils.isNotBlank(redirectUri), "redirectUri cannot be blank");
    this.appConfig = appConfig;
    this.accessTokenManager = accessTokenManager;
    this.redirectUri = URLCodecUtils.encode(redirectUri, "utf-8");
  }

  public static AuthorizationProcessorImpl create(AppConfig appConfig, AccessTokenManager accessTokenManager, String redirectUri) {
    return new AuthorizationProcessorImpl(appConfig, accessTokenManager, redirectUri);
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
  public AccessToken handleAuthorized(String authorizationCode, String state) {
    // 暂时先不管第二个参数
    return handleAuthorized(authorizationCode);
  }

  @Override
  public AccessToken handleAuthorized(String authorizationCode) {
    AccessToken accessToken;
    try {
      accessToken = getRemoteAccessToken(authorizationCode);
    } catch (Exception e) {
      throw new IllegalStateException("Could not handle authorized:: authorization code " + authorizationCode);
    }
    return accessTokenManager.saveAccessToken(accessToken);
  }

  /**
   * 通过授权码获取远程的访问令牌
   *
   * @param authorizationCode 授权码
   * @return 获取远程的访问令牌
   * @throws WebClientException 请求远程数据失败
   * @throws ResponseParseException 转换请求结果失败
   */
  private AccessToken getRemoteAccessToken(String authorizationCode) throws WebClientException, ResponseParseException {
    HttpGet req = new HttpGet(getAccessTokenUri(appConfig.getAppKey(), appConfig.getAppSecret(), authorizationCode));
    String rawString = webClient.execute(req);
    DefaultResponse response = DefaultResponse.create(rawString);
    if (response.isFailed()) {
      throw new IllegalStateException("Could not get remote access token, " + response.getMessage() + ":: authorization code "
              + authorizationCode);
    }
    Map<String, Object> accessTokenData = response.parseMapping();
    String accessToken = MapUtils.getString(accessTokenData, "access_token");
    String refreshToken = MapUtils.getString(accessTokenData, "refresh_token");
    String scope = MapUtils.getString(accessTokenData, "scope");
    String shopId = MapUtils.getString(accessTokenData, "shop_id");
    String shopName = MapUtils.getString(accessTokenData, "shop_name");
    Long expiresIn = MapUtils.getLong(accessTokenData, "expires_in");
    if (StringUtils.isAnyBlank(accessToken, refreshToken, shopId) || Objects.isNull(expiresIn)) {
      throw new IllegalStateException("Could not get valid remote access token:: authorization code " + authorizationCode);
    }
    return AccessToken.builder()
            .setAccessToken(accessToken).setRefreshToken(refreshToken)
            .setScope(scope).setShopId(shopId).setShopName(shopName)
            .setExpiresIn(expiresIn).setExpiresTimestamp(System.currentTimeMillis() / 1000 + expiresIn)
            .setUpdateTime(new Date())
            .build();
  }
}
