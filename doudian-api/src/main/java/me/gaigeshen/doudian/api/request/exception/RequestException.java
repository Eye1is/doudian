package me.gaigeshen.doudian.api.request.exception;

/**
 * @author gaigeshen
 */
public class RequestException extends Exception {

  public RequestException() {
    super();
  }

  public RequestException(String message) {
    super(message);
  }

  public RequestException(String message, Throwable cause) {
    super(message, cause);
  }
}
