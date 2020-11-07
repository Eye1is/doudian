package me.gaigeshen.doudian.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author gaigeshen
 */
@ConfigurationProperties("doudian")
public class DoudianProperties {
  /** The application key or id */
  private String appKey;
  /** The application secret */
  private String appSecret;
  /** The authorization config */
  private Authorize authorize = new Authorize();

  public String getAppKey() {
    return appKey;
  }

  public void setAppKey(String appKey) {
    this.appKey = appKey;
  }

  public String getAppSecret() {
    return appSecret;
  }

  public void setAppSecret(String appSecret) {
    this.appSecret = appSecret;
  }

  public Authorize getAuthorize() {
    return authorize;
  }

  public void setAuthorize(Authorize authorize) {
    this.authorize = authorize;
  }

  /**
   * @author gaigeshen
   */
  public static class Authorize {
    /** Authorize redirect uri */
    private String redirectUri;
    /** Data source bean name, for access token persistence */
    private String dataSource;

    public String getRedirectUri() {
      return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
      this.redirectUri = redirectUri;
    }

    public String getDataSource() {
      return dataSource;
    }

    public void setDataSource(String dataSource) {
      this.dataSource = dataSource;
    }
  }
}
