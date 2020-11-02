package me.gaigeshen.doudian.api;

import me.gaigeshen.doudian.api.param.Params;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Objects;

/**
 * @author gaigeshen
 */
public class Request {

  private final String method;

  private final Params params;

  private Request(String method, Params params) {
    Validate.isTrue(StringUtils.isNotBlank(method), "method");
    Validate.isTrue(Objects.nonNull(params), "params");
    this.method = method;
    this.params = params;
  }

  public static Request create(String method, Params params) {
    return new Request(method, params);
  }

  public String getMethod() {
    return method;
  }

  public Params getParams() {
    return params;
  }

  public String getParamsJson() {
    return params.toJson();
  }
}
