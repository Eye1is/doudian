package me.gaigeshen.doudian.api.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author gaigeshen
 */
public class URLCodecUtils {

  private URLCodecUtils() {}

  public static String encode(String url, String charset) {
    try {
      return URLEncoder.encode(url, charset);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Could not encode " + url + " with charset " + charset, e);
    }
  }

  public static String decode(String url, String charset) {
    try {
      return URLDecoder.decode(url, charset);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Could not decode " + url + " with charset " + charset, e);
    }
  }
}
