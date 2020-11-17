package me.gaigeshen.doudian.api.http;

import org.apache.http.entity.ContentType;

import java.io.InputStream;
import java.util.Map;

/**
 * The request content
 *
 * @author gaigeshen
 */
public interface RequestContent {
  /**
   * Returns request uri
   *
   * @return The request uri
   */
  String getUri();

  /**
   * Returns request method, same as http request method
   *
   * @return Request method
   */
  String getMethod();

  /**
   * Returns request content type, same as http mime type
   *
   * @return Request content type
   */
  Type getType();

  /**
   * Returns encoding, like 'utf-8'
   *
   * @return Encoding
   */
  String getEncoding();

  /**
   * Returns request content text value
   *
   * @return Content text value
   */
  String getText();

  /**
   * Returns request content binary value
   *
   * @return Content binary value
   */
  byte[] getBinary();

  /**
   * Returns request content input stream object
   *
   * @return Content input stream object
   */
  InputStream getStream();

  /**
   * Returns request content parameters object
   *
   * @return Content parameters obejct
   */
  Map<String, String> getParameters();

  /**
   * Returns request content multipart parameters object
   *
   * @return Content multipart parameters object, value of this object can only support byte[], file, input stream and string value
   */
  Map<String, Object> getMultipartParameters();

  /**
   * Request content type, only support six type
   *
   * @author gaigeshen
   */
  enum Type {

    TEXT_PLAIN("text/plain"),

    TEXT_JSON("application/json"),

    BINARY("application/octet-stream"),

    STREAM("application/octet-stream"),

    PARAMETERS("application/x-www-form-urlencoded"),

    MULTIPART_PARAMETERS("multipart/form-data");

    private final String value;

    Type(String name) {
      this.value = name;
    }

    /**
     * Returns name, same as http mime type
     *
     * @return The name
     */
    public String getName() {
      return value;
    }

    /**
     * Parse to content type
     *
     * @return Content type
     */
    public ContentType parseContentType() {
      return ContentType.create(value);
    }

    /**
     * Parse to content type
     *
     * @param charset The charset, like 'utf-8'
     * @return Content type
     */
    public ContentType parseContentType(String charset) {
      return ContentType.create(value, charset);
    }

    /**
     * Parse to type enum with name
     *
     * @param name The name same as http mime type
     * @return The type enum
     */
    public Type parseFromName(String name) {
      for (Type type : values()) {
        if (type.value.equals(name)) {
          return type;
        }
      }
      throw new IllegalArgumentException("Invalid name:: " + name);
    }

    @Override
    public String toString() {
      return value;
    }
  }

}
