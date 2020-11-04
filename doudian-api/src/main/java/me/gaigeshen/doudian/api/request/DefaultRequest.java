package me.gaigeshen.doudian.api.request;

import me.gaigeshen.doudian.api.Constants;
import me.gaigeshen.doudian.api.request.param.Params;

/**
 * 默认的请求数据，该请求数据表达的是业务请求
 *
 * @author gaigeshen
 */
public class DefaultRequest implements Request {

  private final Params params;

  private DefaultRequest(Params params) {
    this.params = params;
  }

  public static DefaultRequest create(Params params) {
    return new DefaultRequest(params);
  }

  @Override
  public String getUri() {
    // 业务请求的链接地址是固定的
    return Constants.API_URI;
  }

  @Override
  public Params getParams() {
    return params;
  }
}
