package me.gaigeshen.doudian.api.authorization;

import java.io.Closeable;

/**
 * 访问令牌管理器
 *
 * @author gaigeshen
 */
public interface AccessTokenManager extends Closeable {
  /**
   * 初始化
   */
  void init();

  /**
   * 保存访问令牌，店铺授权之后得到访问令牌调用此方法，将该访问令牌保存
   *
   * @param accessToken 访问令牌
   */
  void persistAccessToken(AccessToken accessToken);

  /**
   * 删除访问令牌，后续会停止该店铺对应的访问令牌的更新
   *
   * @param shopId 店铺编号
   */
  void deleteAccessToken(String shopId);

  /**
   * 获取当前的访问令牌
   *
   * @param shopId 店铺编号
   * @return 当前的访问令牌
   */
  AccessToken getAccessToken(String shopId);
}
