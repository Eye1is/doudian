package me.gaigeshen.doudian.api.request;

/**
 * 请求执行异常
 *
 * @author gaigeshen
 */
public class RequestExecutionException extends Exception {

  public RequestExecutionException(String message) {
    super(message);
  }

  public RequestExecutionException(String message, Throwable cause) {
    super(message, cause);
  }
}
