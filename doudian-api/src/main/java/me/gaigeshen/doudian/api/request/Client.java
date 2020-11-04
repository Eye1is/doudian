package me.gaigeshen.doudian.api.request;

/**
 * 请求客户端
 *
 * @author gaigeshen
 */
public interface Client {
  /**
   * 执行请求
   *
   * @param req 请求数据
   * @return 返回响应结果
   */
  Response execute(Request req);

}
