package me.gaigeshen.doudian.api.request;

/**
 * 表达的是某个请求中的数据
 *
 * @author gaigeshen
 */
public interface Request {
  /**
   * 返回请求链接
   *
   * @return 请求链接
   */
  String getUri();

  /**
   * 返回店铺编号
   *
   * @return 店铺编号
   */
  String getShopId();

  /**
   * 返回请求中的业务参数数据
   *
   * @return 请求中的业务参数数据
   */
  Params getParams();
}
