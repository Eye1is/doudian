package me.gaigeshen.doudian.api.request;

import java.util.Map;

/**
 * 表达的是请求响应结果
 *
 * @author gaigeshen
 */
public interface Response {
  /**
   * 返回原始的响应字符串内容
   *
   * @return 原始的响应字符串内容
   */
  String getRawString();

  /**
   * 返回业务数据的响应字符串内容
   *
   * @return 业务数据的响应字符串内容
   */
  String getResultRawString();

  /**
   * 返回是否是业务失败的响应，此处的失败表达的是业务方面的失败与否
   *
   * @return 是否是失败的响应
   */
  boolean isFailed();

  /**
   * 如果为业务失败的响应，可能会有失败的消息内容
   *
   * @return 消息内容
   */
  String getMessage();

  /**
   * 将响应的业务数据转换为映射对象
   *
   * @return 映射对象
   * @throws ResponseParseException 转换异常
   */
  Map<String, Object> parseMapping() throws ResponseParseException;

}
