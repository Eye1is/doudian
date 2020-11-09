package me.gaigeshen.doudian.api.request.sign;

/**
 * 签名生成器
 *
 * @author gaigeshen
 */
public interface SignGenerator {

  /**
   * 生成签名
   *
   * @param signParams 签名所需要的参数
   * @return 签名内容
   */
  String generate(SignParams signParams);

}
