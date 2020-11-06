package me.gaigeshen.doudian.api.authorization;

/**
 * 授权流程处理器
 *
 * @author gaigeshen
 */
public interface AuthorizationProcessor {
  /**
   * 获取授权链接
   *
   * @param state 授权回调需要携带的数据
   * @return 授权链接
   */
  String getAuthorizeUri(String state);

  /**
   * 获取授权链接，授权回调需要携带的数据为空字符串
   *
   * @return 授权链接
   */
  String getAuthorizeUri();

  /**
   * 处理授权后的授权码
   *
   * @param authorizationCode 授权码
   * @param state 授权回调需要携带的数据
   * @return 处理授权码得到的访问令牌
   */
  AccessToken handleAuthorized(String authorizationCode, String state);

  /**
   * 处理授权后的授权码，忽略授权回调需要携带的数据
   *
   * @param authorizationCode 授权码
   * @return 处理授权码得到的访问令牌
   */
  AccessToken handleAuthorized(String authorizationCode);
}
