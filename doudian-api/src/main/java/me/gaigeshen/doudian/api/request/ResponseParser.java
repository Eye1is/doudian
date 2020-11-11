package me.gaigeshen.doudian.api.request;

/**
 * 请求响应结果转换器
 *
 * @author gaigeshen
 */
public interface ResponseParser<T> {
  /**
   * 转换请求响应结果
   *
   * @param resp 请求响应结果
   * @return 转换之后的目标
   * @throws ResponseParseException 转换过程中发生异常
   * @throws RequestResultException 转换成功但是该响应结果为失败
   */
  T parse(Response resp) throws ResponseParseException, RequestResultException;

}
