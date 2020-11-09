package me.gaigeshen.doudian.api.request;

import me.gaigeshen.doudian.api.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Objects;

/**
 * 默认的请求数据，该请求数据表达的是业务请求
 *
 * @author gaigeshen
 */
public class DefaultRequest implements Request {
  private final String shopId;
  private final Params params;

  private DefaultRequest(String shopId, Params params) {
    this.shopId = shopId;
    this.params = params;
  }

  public static DefaultRequest create(String shopId, Params params) {
    Validate.isTrue(StringUtils.isNotBlank(shopId), "shopId");
    Validate.isTrue(Objects.nonNull(params), "params");
    return new DefaultRequest(shopId, params);
  }

  @Override
  public String getUri() {
    return Constants.API_URI;
  }

  @Override
  public String getShopId() {
    return shopId;
  }

  @Override
  public Params getParams() {
    return params;
  }
}
