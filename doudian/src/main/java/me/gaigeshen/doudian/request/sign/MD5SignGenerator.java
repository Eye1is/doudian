package me.gaigeshen.doudian.api.request.sign;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author gaigeshen
 */
public class MD5SignGenerator extends AbstractSignGenerator {

  private MD5SignGenerator() { }

  private static class InstanceHolder {
    private static final MD5SignGenerator INSTANCE = new MD5SignGenerator();
  }

  public static MD5SignGenerator getInstance() {
    return InstanceHolder.INSTANCE;
  }

  @Override
  protected String calculate(String algorithmInputValue) {
    return DigestUtils.md5Hex(algorithmInputValue);
  }
}
