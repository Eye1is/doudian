package me.gaigeshen.doudian.api.request.logistics;

import me.gaigeshen.doudian.api.request.Params;

/**
 * @author gaigeshen
 */
public class CompanyParams implements Params {
  @Override
  public String getMethod() {
    return "order.logisticsCompanyList";
  }
}
