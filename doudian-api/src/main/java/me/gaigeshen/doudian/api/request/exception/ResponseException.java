package me.gaigeshen.doudian.api.request.exception;

/**
 * @author gaigeshen
 */
public class ResponseException extends Exception {
  public ResponseException() {
  }

  public ResponseException(String message) {
    super(message);
  }

  public ResponseException(String message, Throwable cause) {
    super(message, cause);
  }
}
