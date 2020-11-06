package me.gaigeshen.doudian.api;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * @author gaigeshen
 */
public class AppConfig {

  private final String appKey;

  private final String appSecret;

  public AppConfig(String appKey, String appSecret) {
    Validate.isTrue(StringUtils.isNotBlank(appKey), "appKey cannot be blank");
    Validate.isTrue(StringUtils.isNotBlank(appSecret), "appKey cannot be blank");
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
