package me.gaigeshen.doudian.api;

/**
 * 模板链接地址常量
 *
 * @author gaigeshen
 */
public class UriConstants {

  /** 接口请求地址 */
  public static final String API_URI = "https://openapi-fxg.jinritemai.com";

  /** 店铺授权地址 */
  public static final String SHOP_AUTHORIZE_TEMPLATE_URI = "https://fxg.jinritemai.com/index.html#/ffa/open/applicationAuthorize?response_type=code&app_id=%s&redirect_uri=%s&state=%s";

  /** 访问令牌地址 */
  public static final String ACCESS_TOKEN_TEMPLATE_URI = "https://openapi-fxg.jinritemai.com/oauth2/access_token?app_id=%s&app_secret=%s&code=%s&grant_type=authorization_code";

  /** 刷新访问令牌地址 */
  public static final String ACCESS_TOKEN_REFRESH_TEMPLATE_URI = "https://openapi-fxg.jinritemai.com/oauth2/refresh_token?app_id=%s&app_secret=%s&grant_type=refresh_token&refresh_token=%s";

  private UriConstants() { }

  private static String getUri(String templateUri, Object... values) {
    return String.format(templateUri, values);
  }

  public static String getShopAuthorizeUri(String appKey, String redirectUri, String state) {
    return getUri(SHOP_AUTHORIZE_TEMPLATE_URI, appKey, redirectUri, state);
  }

  public static String getAccessTokenUri(String appKey, String appSecret, String code) {
    return getUri(ACCESS_TOKEN_TEMPLATE_URI, appKey, appSecret, code);
  }

  public static String getAccessTokenRefreshUri(String appKey, String appSecret, String refreshToken) {
    return getUri(ACCESS_TOKEN_REFRESH_TEMPLATE_URI, appKey, appSecret, refreshToken);
  }

}
