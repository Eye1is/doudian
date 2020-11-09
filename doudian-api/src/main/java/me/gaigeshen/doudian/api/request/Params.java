package me.gaigeshen.doudian.api.request;

/**
 * 业务参数，表达的是某个业务需要的所有参数和参数值
 *
 * @author gaigeshen
 */
public interface Params {
  /**
   * 该业务参数对应的接口名称
   *
   * @return 接口名称
   */
  String getMethod();
}
