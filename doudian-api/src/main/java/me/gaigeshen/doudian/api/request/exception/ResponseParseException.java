package me.gaigeshen.doudian.api.request.exception;

/**
 * @author gaigeshen
 */
public class ResponseParseException extends ResponseException {
  public ResponseParseException() {
  }

  public ResponseParseException(String message) {
    super(message);
  }

  public ResponseParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
