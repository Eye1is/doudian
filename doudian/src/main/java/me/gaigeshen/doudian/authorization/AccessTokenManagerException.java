package me.gaigeshen.doudian.api.authorization;

/**
 * 访问令牌管理器异常
 *
 * @author gaigeshen
 */
public class AccessTokenManagerException extends Exception {
  public AccessTokenManagerException(String message) {
    super(message);
  }
  public AccessTokenManagerException(String message, Throwable cause) {
    super(message, cause);
  }
}
