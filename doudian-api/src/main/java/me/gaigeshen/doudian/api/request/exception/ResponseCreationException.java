package me.gaigeshen.doudian.api.request.exception;

/**
 * @author gaigeshen
 */
public class ResponseCreationException extends ResponseException {
  public ResponseCreationException() {
  }

  public ResponseCreationException(String message) {
    super(message);
  }

  public ResponseCreationException(String message, Throwable cause) {
    super(message, cause);
  }
}
