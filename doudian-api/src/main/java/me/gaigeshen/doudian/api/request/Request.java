package me.gaigeshen.doudian.api.request;

import me.gaigeshen.doudian.api.request.param.Params;

/**
 * 表达的是某个请求中的数据
 *
 * @author gaigeshen
 */
public interface Request {
  /**
   * 请求链接
   */
  String getUri();

  /**
   * 请求中的业务参数数据
   */
  Params getParams();
}
