package me.gaigeshen.doudian.api.request.sign;

/**
 * 签名方式
 *
 * @author gaigeshen
 */
public enum SignMethod {

  MD5("MD5"),

  HMAC_SHA_256("HMAC-SHA-256");


  private final String name; // 名称

  SignMethod(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  /**
   * 通过名称获取签名方式枚举
   *
   * @param name 名称
   * @return 签名方式
   */
  public static SignMethod parseFromName(String name) {
    for (SignMethod value : SignMethod.values()) {
      if (value.getName().equals(name)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Invalid name, only support MD5 and HMAC-SHA-256");
  }
}
