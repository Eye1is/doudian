package me.gaigeshen.doudian.api.request;

import me.gaigeshen.doudian.api.request.param.Params;

/**
 * 简单请求的数据，仅包含请求链接地址
 *
 * @author gaigeshen
 */
public class SimpleRequest implements Request {

  private final String uri;

  private SimpleRequest(String uri) {
    this.uri = uri;
  }

  public static SimpleRequest create(String uri) {
    return new SimpleRequest(uri);
  }

  @Override
  public String getUri() {
    return uri;
  }

  /**
   * 由于是简单请求，所以返回的业务参数为空
   */
  @Override
  public Params getParams() {
    return null;
  }
}
