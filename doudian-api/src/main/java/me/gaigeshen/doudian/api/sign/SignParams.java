package me.gaigeshen.doudian.api.sign;

import me.gaigeshen.doudian.api.param.Params;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Objects;

/**
 * @author gaigeshen
 */
public class SignParams {

  private final String appKey;

  private final String appSecret;

  private final String method;

  private final Params params;

  private final String timestamp;

  private final String version;

  private SignParams(Builder builder) {
    this.appKey = builder.appKey;
    this.appSecret = builder.appSecret;
    this.method = builder.method;
    this.params = builder.params;
    this.timestamp = builder.timestamp;
    this.version = builder.version;
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

  public String getMethod() {
    return method;
  }

  public Params getParams() {
    return params;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public String getVersion() {
    return version;
  }

  /**
   * @author gaigeshen
   */
  private static class Builder {

    private String appKey;

    private String appSecret;

    private String method;

    private Params params;

    private String timestamp;

    private String version;

    public Builder appKey(String appKey) {
      this.appKey = appKey;
      return this;
    }

    public Builder appSecret(String appSecret) {
      this.appSecret = appSecret;
      return this;
    }

    public Builder method(String method) {
      this.method = method;
      return this;
    }

    public Builder params(Params params) {
      this.params = params;
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

    public SignParams build() {
      Validate.isTrue(StringUtils.isNotBlank(appKey), "appKey");
      Validate.isTrue(StringUtils.isNotBlank(appSecret), "appSecret");
      Validate.isTrue(StringUtils.isNotBlank(method), "method");
      Validate.isTrue(Objects.nonNull(params), "params");
      Validate.isTrue(StringUtils.isNotBlank(timestamp), "timestamp");
      Validate.isTrue(StringUtils.isNotBlank(version), "version");
      return new SignParams(this);
    }
  }
}
