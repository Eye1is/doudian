package me.gaigeshen.doudian.api.authorization;

import java.util.List;

/**
 * 访问令牌管理器，内部需要访问令牌存储器用于访问令牌的存储和获取，同时此访问令牌管理器维护所有访问令牌的更新
 *
 * @author gaigeshen
 */
public interface AccessTokenManager {
  /**
   * 返回内部使用的访问令牌存储器
   *
   * @return 访问令牌存储器
   */
  AccessTokenStore getAccessTokenStore();

  /**
   * 保存访问令牌，保存旧店铺的访问令牌也可以调用此方法
   *
   * @param accessToken 访问令牌
   * @return 该访问令牌
   */
  AccessToken saveAccessToken(AccessToken accessToken);

  /**
   * 查询访问令牌
   *
   * @param shopId 店铺编号
   * @return 该店铺的访问令牌
   */
  AccessToken findAccessToken(String shopId);

  /**
   * 查询所有的访问令牌
   *
   * @return 所有的访问令牌
   */
  List<AccessToken> findAccessTokens();

  /**
   * 删除访问令牌
   *
   * @param shopId 店铺编号
   */
  void deleteAccessToken(String shopId);

  /**
   * 启动此访问令牌管理器
   */
  void startup();

  /**
   * 关闭此访问令牌管理器
   */
  void shutdown();

}
