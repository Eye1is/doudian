package me.gaigeshen.doudian.api.request.shop;

import me.gaigeshen.doudian.api.request.Params;

/**
 * @author gaigeshen
 */
public class BrandParams implements Params {

  @Override
  public String getMethod() {
    return "shop.brandList";
  }

  private final Long id;

  private final String brandChineseName;

  private final String brandEnglishName;

  private final String brandRegNum;

  private BrandParams(Builder builder) {
    this.id = builder.id;
    this.brandChineseName = builder.brandChineseName;
    this.brandEnglishName = builder.brandEnglishName;
    this.brandRegNum = builder.brandRegNum;
  }

  public Long getId() {
    return id;
  }

  public String getBrandChineseName() {
    return brandChineseName;
  }

  public String getBrandEnglishName() {
    return brandEnglishName;
  }

  public String getBrandRegNum() {
    return brandRegNum;
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * @author gaigeshen
   */
  public static class Builder {

    private Long id;

    private String brandChineseName;

    private String brandEnglishName;

    private String brandRegNum;

    public Builder setId(Long id) {
      this.id = id;
      return this;
    }

    public Builder setBrandChineseName(String brandChineseName) {
      this.brandChineseName = brandChineseName;
      return this;
    }

    public Builder setBrandEnglishName(String brandEnglishName) {
      this.brandEnglishName = brandEnglishName;
      return this;
    }

    public Builder setBrandRegNum(String brandRegNum) {
      this.brandRegNum = brandRegNum;
      return this;
    }

    public BrandParams build() {
      return new BrandParams(this);
    }
  }
}
