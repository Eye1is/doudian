package me.gaigeshen.doudian.api.http;

/**
 * @author gaigeshen
 */
public class WebClientException extends Exception {
  public WebClientException() {
    super();
  }

  public WebClientException(String message) {
    super(message);
  }

  public WebClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
