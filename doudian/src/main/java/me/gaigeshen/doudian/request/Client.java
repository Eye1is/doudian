package me.gaigeshen.doudian.api.request;

import java.util.List;

/**
 * 请求客户端
 *
 * @author gaigeshen
 */
public interface Client {

  /**
   * 执行请求并返回响应结果
   *
   * @param req 请求对象
   * @return 响应结果
   * @throws RequestExecutionException 请求执行异常
   */
  Response execute(Request req) throws RequestExecutionException;

  /**
   * 执行请求并返回业务响应结果
   *
   * @param req 请求对象
   * @param resultClass 业务响应结果对象类型
   * @param <T> 表示业务响应对象类型
   * @return 业务响应结果
   * @throws RequestExecutionException 请求执行异常
   */
  <T extends Result> T executeResult(Request req, Class<T> resultClass) throws RequestExecutionException;

  /**
   * 执行请求并返回业务响应结果
   *
   * @param req 请求对象
   * @param resultClass 业务响应结果对象类型
   * @param <T> 表示业务响应对象类型
   * @return 业务响应结果
   * @throws RequestExecutionException 请求执行异常
   */
  <T extends Result> List<T> executeResults(Request req, Class<T> resultClass) throws RequestExecutionException;
}
