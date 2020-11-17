package me.gaigeshen.doudian.api.http;


import me.gaigeshen.doudian.api.util.Asserts;

import java.io.InputStream;
import java.util.Map;

/**
 * The default request content
 *
 * @author gaigeshen
 */
public class RequestContentImpl implements RequestContent {

  private final String uri;

  private final String method;

  private final Type type;

  private final String encoding;

  private final String text;

  private final byte[] binary;

  private final InputStream stream;

  private final Map<String, String> parameters;

  private final Map<String, Object> multipartParameters;

  private RequestContentImpl(Builder builder) {
    this.uri = builder.uri;
    this.method = builder.method;
    this.type = builder.type;
    this.encoding = builder.encoding;
    this.text = builder.text;
    this.binary = builder.binary;
    this.stream = builder.stream;
    this.parameters = builder.parameters;
    this.multipartParameters = builder.multipartParameters;
  }

  /**
   * Returns builder for build request content object
   *
   * @return The builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns builder for build request content object, build json type request content
   *
   * @param text Json string value
   * @return The builder
   */
  public static Builder createJson(String text) {
    return builder().setType(Type.TEXT_JSON).setText(text);
  }

  /**
   * Returns builder for build request content object, build parameters type request content
   *
   * @param parameters Parameters value
   * @return The builder
   */
  public static Builder createParameters(Map<String, String> parameters) {
    return builder().setType(Type.PARAMETERS).setParameters(parameters);
  }

  /**
   * eturns builder for build request content object, build multipart type request content
   *
   * @param multipartParameters Multipart parameters value
   * @return The builder
   */
  public static Builder createMultipartParameters(Map<String, Object> multipartParameters) {
    return builder().setType(Type.MULTIPART_PARAMETERS).setMultipartParameters(multipartParameters);
  }

  @Override
  public String getUri() {
    return uri;
  }

  @Override
  public String getMethod() {
    return method;
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public String getEncoding() {
    return encoding;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public byte[] getBinary() {
    return binary;
  }

  @Override
  public InputStream getStream() {
    return stream;
  }

  @Override
  public Map<String, String> getParameters() {
    return parameters;
  }

  @Override
  public Map<String, Object> getMultipartParameters() {
    return multipartParameters;
  }

  /**
   * The builder for build request content
   *
   * @author gaigeshen
   */
  public static class Builder {

    private String uri;

    private String method;

    private Type type;

    private String encoding;

    private String text;

    private byte[] binary;

    private InputStream stream;

    private Map<String, String> parameters;

    private Map<String, Object> multipartParameters;

    public Builder setUri(String uri) {
      this.uri = Asserts.notBlank(uri, "uri");
      return this;
    }

    public Builder setMethod(String method) {
      this.method = Asserts.notBlank(method, "method");
      return this;
    }

    public Builder setType(Type type) {
      this.type = Asserts.notNull(type, "type");
      return this;
    }

    public Builder setEncoding(String encoding) {
      this.encoding = Asserts.notBlank(encoding, "encoding");
      return this;
    }

    public Builder setText(String text) {
      this.text = Asserts.notBlank(text, "text");
      return this;
    }

    public Builder setBinary(byte[] binary) {
      this.binary = Asserts.notNull(binary, "binary");
      return this;
    }

    public Builder setStream(InputStream stream) {
      this.stream = Asserts.notNull(stream, "stream");
      return this;
    }

    public Builder setParameters(Map<String, String> parameters) {
      this.parameters = Asserts.notNull(parameters, "parameters");
      return this;
    }

    public Builder setMultipartParameters(Map<String, Object> multipartParameters) {
      this.multipartParameters = Asserts.notNull(multipartParameters, "multipartParameters");
      return this;
    }

    /**
     * Build method
     *
     * @return Request content
     */
    public RequestContentImpl build() {
      return new RequestContentImpl(this);
    }
  }
}
