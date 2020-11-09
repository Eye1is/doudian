package me.gaigeshen.doudian.api.request.sign;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Objects;

/**
 * 用于签名的参数
 *
 * @author gaigeshen
 */
public class SignParams {

  private final String appKey;

  private final String appSecret;

  private final String timestamp;

  private final String version;

  private final String params;

  private SignParams(Builder builder) {
    this.appKey = builder.appKey;
    this.appSecret = builder.appSecret;
    this.timestamp = builder.timestamp;
    this.version = builder.version;
    this.params = builder.params;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getAppKey() {
    return appKey;
  }

  public String getAppSecret() {
    return appSecret;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public String getVersion() {
    return version;
  }

  public String getParams() {
    return params;
  }

  /**
   * 签名的参数构建器
   *
   * @author gaigeshen
   */
  public static class Builder {

    private String appKey;

    private String appSecret;

    private String timestamp;

    private String version;

    private String params;

    public Builder appKey(String appKey) {
      this.appKey = appKey;
      return this;
    }

    public Builder appSecret(String appSecret) {
      this.appSecret = appSecret;
      return this;
    }

    public Builder timestamp(String timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder version(String version) {
      this.version = version;
      return this;
    }

    public Builder params(String params) {
      this.params = params;
      return this;
    }

    public SignParams build() {
      Validate.isTrue(StringUtils.isNotBlank(appKey), "appKey");
      Validate.isTrue(StringUtils.isNotBlank(appSecret), "appSecret");
      Validate.isTrue(StringUtils.isNotBlank(timestamp), "timestamp");
      Validate.isTrue(StringUtils.isNotBlank(version), "version");
      Validate.isTrue(Objects.nonNull(params), "params");
      return new SignParams(this);
    }
  }
}
