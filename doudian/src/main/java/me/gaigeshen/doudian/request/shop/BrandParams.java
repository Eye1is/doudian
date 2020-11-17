package me.gaigeshen.doudian.api.request.shop;

import me.gaigeshen.doudian.api.request.Params;

/**
 * 店铺已授权品牌参数
 *
 * @author gaigeshen
 */
public class BrandParams implements Params {
  @Override
  public String getMethod() {
    return "shop.brandList";
  }
}
