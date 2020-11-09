package me.gaigeshen.doudian.api.request.sign;

import java.util.Objects;

/**
 * 用于签名生成器的获取
 *
 * @author gaigeshen
 */
public class SignGeneratorFactory {

  public static final SignMethod DEFAULT_SIGN_METHOD = SignMethod.MD5;

  private SignGeneratorFactory() {}

  /**
   * 获取签名生成器
   *
   * @param signMethod 签名方式
   * @return 签名生成器
   */
  public static SignGenerator getGenerator(SignMethod signMethod) {
    if (Objects.nonNull(signMethod)) {
      if (SignMethod.MD5 == signMethod) {
        return MD5SignGenerator.getInstance();
      }
      throw new IllegalArgumentException("Only support MD5 sign method");
    }
    return getGenerator(DEFAULT_SIGN_METHOD);
  }
}
