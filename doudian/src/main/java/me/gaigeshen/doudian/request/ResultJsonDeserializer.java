package me.gaigeshen.doudian.api.request;

import java.util.List;

/**
 * 用于反序列化请求执行结果中的业务结果
 *
 * @author gaigeshen
 */
public interface ResultJsonDeserializer {

  /**
   * 反序列化业务结果
   *
   * @param resultRawString 原始的业务结果字符串内容
   * @param targetClass 序列化目标业务结果对象类型
   * @param <T> 表示业务结果对象类型
   * @return 业务结果
   */
  <T extends Result> T deserializeResult(String resultRawString, Class<T> targetClass);

  /**
   * 反序列化业务结果
   *
   * @param resultRawString 原始的业务结果字符串内容
   * @param targetItemClass 序列化目标业务结果对象类型
   * @param <T> 表示业务结果对象类型
   * @return 业务结果
   */
  <T extends Result> List<T> deserializeResults(String resultRawString, Class<T> targetItemClass);

}
