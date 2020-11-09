package me.gaigeshen.doudian.api.request;

/**
 * 业务参数序列化器，序列化为字符串
 *
 * @author gaigeshen
 */
public interface ParamsJsonSerializer {
  /**
   * 执行序列化业务参数，所有的业务参数值类型保持不变
   *
   * @param params 业务参数
   * @return 序列化结果
   */
  String serializer(Params params);

  /**
   * 执行序列化业务参数
   *
   * @param params 业务参数
   * @param stringifyValues 是否将所有的业务参数值转变为字符串
   * @return 序列化结果
   */
  String serializer(Params params, boolean stringifyValues);
}
