package me.gaigeshen.doudian.api.http;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Response content, include raw data
 *
 * @author gaigeshen
 */
public interface ResponseContent {
  /**
   * Returns raw data
   *
   * @return Raw data bytes
   */
  byte[] getRawBytes();

  /**
   * Returns response content type, same as http mime type
   *
   * @return Response content type
   */
  String getType();

  /**
   * Parse raw data to string value, use encoding from content type, or iso-8859-1
   *
   * @return String value
   */
  String getAsString();

  /**
   * Parse raw data to string value, use charset parameter
   *
   * @param charset Charset parameter
   * @return String value
   */
  String getAsString(Charset charset);

  /**
   * Parse raw data to new input stream, close this stream after used by caller
   *
   * @return Input stream
   */
  InputStream getAsStream();

}
