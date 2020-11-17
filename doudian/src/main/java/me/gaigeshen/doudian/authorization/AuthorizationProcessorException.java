package me.gaigeshen.doudian.api.authorization;

/**
 * @author gaigeshen
 */
public class AuthorizationProcessorException extends Exception {
  public AuthorizationProcessorException(String message) {
    super(message);
  }
  public AuthorizationProcessorException(String message, Throwable cause) {
    super(message, cause);
  }
}
