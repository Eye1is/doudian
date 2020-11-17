package me.gaigeshen.doudian.api.request.shop;

import lombok.Getter;
import lombok.Setter;
import me.gaigeshen.doudian.api.request.Result;

/**
 * 店铺已授权品牌
 *
 * @author gaigeshen
 */
@Getter
@Setter
public class BrandResult implements Result {

  private Long id; // 品牌编号

  private String brandChineseName; // 品牌中文名

  private String brandEnglishName; // 品牌英文名

  private String brandRegNum; // 商标注册号

}
