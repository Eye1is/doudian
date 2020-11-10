package me.gaigeshen.doudian.api.request.logistics;

import lombok.Getter;
import lombok.Setter;
import me.gaigeshen.doudian.api.request.Result;

/**
 * @author gaigeshen
 */
@Getter
@Setter
public class CompanyResult implements Result {

  private Long id; // 快递公司编号

  private String name; // 快递公司名称

}
