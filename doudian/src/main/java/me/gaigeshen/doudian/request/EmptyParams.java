package me.gaigeshen.doudian.api.request;

/**
 * 空的业务参数
 *
 * @author gaigeshen
 */
public class EmptyParams implements Params {

  private final String method;

  public EmptyParams(String method) {
    this.method = method;
  }

  public EmptyParams() {
    this.method = "";
  }

  @Override
  public String getMethod() {
    return method;
  }
}
