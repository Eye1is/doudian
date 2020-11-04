package me.gaigeshen.doudian.api.request.param;

/**
 * @author gaigeshen
 */
public class DefaultParams extends AbstractParams {

  private final String method;

  private DefaultParams(String method) {
    this.method = method;
  }

  public static DefaultParams create(String method) {
    return new DefaultParams(method);
  }

  @Override
  public String getMethod() {
    return method;
  }
}
