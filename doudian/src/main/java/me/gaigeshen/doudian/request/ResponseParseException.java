package me.gaigeshen.doudian.api.request;

/**
 * 转换请求执行结果的时候发生异常
 *
 * @author gaigeshen
 */
public class ResponseParseException extends RequestExecutionException {

  public ResponseParseException(String message) {
    super(message);
  }

  public ResponseParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
