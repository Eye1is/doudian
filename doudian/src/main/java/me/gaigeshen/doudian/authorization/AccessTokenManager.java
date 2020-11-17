package me.gaigeshen.doudian.api.authorization;

import java.util.List;

/**
 * 访问令牌管理器，内部需要访问令牌存储器用于访问令牌的存储和获取，同时此访问令牌管理器维护所有访问令牌的更新
 *
 * @author gaigeshen
 */
public interface AccessTokenManager {

  /**
   * 保存访问令牌，保存旧店铺的访问令牌也可以调用此方法
   *
   * @param accessToken 访问令牌
   * @return 该访问令牌
   * @throws AccessTokenManagerException 无法保存访问令牌
   */
  AccessToken saveAccessToken(AccessToken accessToken) throws AccessTokenManagerException;

  /**
   * 查询访问令牌
   *
   * @param shopId 店铺编号
   * @return 该店铺的访问令牌
   * @throws AccessTokenManagerException 无法查询访问令牌
   */
  AccessToken findAccessToken(String shopId) throws AccessTokenManagerException;

  /**
   * 查询所有的访问令牌
   *
   * @return 所有的访问令牌
   * @throws AccessTokenManagerException 无法查询所有的访问令牌
   */
  List<AccessToken> findAccessTokens() throws AccessTokenManagerException;

  /**
   * 删除访问令牌
   *
   * @param shopId 店铺编号
   * @throws AccessTokenManagerException 无法删除访问令牌
   */
  void deleteAccessToken(String shopId) throws AccessTokenManagerException;

  /**
   * 启动此访问令牌管理器，多次启动无效，关闭后无法再次启动
   *
   * @throws AccessTokenManagerException 无法启动此访问令牌管理器
   */
  void startup() throws AccessTokenManagerException;

  /**
   * 关闭此访问令牌管理器，多次关闭无效
   *
   * @throws AccessTokenManagerException 无法关闭此访问令牌管理器
   */
  void shutdown() throws AccessTokenManagerException;

}
