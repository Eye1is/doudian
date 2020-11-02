package me.gaigeshen.doudian.api.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author gaigeshen
 */
public class TimestampUtils {

  public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

  private TimestampUtils() { }

  public static String getCurrentTimestamp() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DEFAULT_FORMAT));
  }
}
