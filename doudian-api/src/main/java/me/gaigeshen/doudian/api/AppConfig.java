package me.gaigeshen.doudian.api;

/**
 * @author gaigeshen
 */
public class AppConfig {

  private final String appKey;

  private final String appSecret;

  public AppConfig(String appKey, String appSecret) {
    this.appKey = appKey;
    this.appSecret = appSecret;
  }

  public String getAppKey() {
    return appKey;
  }

  public String getAppSecret() {
    return appSecret;
  }
}
