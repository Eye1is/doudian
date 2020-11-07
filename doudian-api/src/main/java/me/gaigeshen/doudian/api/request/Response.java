package me.gaigeshen.doudian.api.request;

import java.util.List;
import java.util.Map;

/**
 * 表达的是响应结果
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
   * 返回是否是成功的响应，此处的成功表达的是业务方面的成功与否
   *
   * @return 是否是成功的响应
   */
  boolean isSuccess();

  /**
   * 如果为业务失败的响应，可能会有失败的消息内容
   *
   * @return 消息内容
   */
  String getMessage();

  /**
   * 将响应的数据转换为映射对象
   *
   * @return 映射对象
   */
  Map<String, Object> parseMapping();

  /**
   * 将响应的数据转换为指定类型的对象
   *
   * @param targetClass 指定类型
   * @param <T> 指定类型
   * @return 指定类型的对象
   */
  <T> T parseObject(Class<T> targetClass);

  /**
   * 将响应的数据转换为指定类型的对象集合
   *
   * @param itemClass 集合内的对象类型
   * @param <T> 指定类型
   * @return 指定类型的对象集合
   */
  <T> List<T> parseList(Class<T> itemClass);

}
