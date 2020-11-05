package me.gaigeshen.doudian.api.authorization;

import java.util.List;

/**
 * 访问令牌存储器
 *
 * @author gaigeshen
 */
public interface AccessTokenStore {
  /**
   * 保存或者更新访问令牌，通过返回值确定本次操作的是否为新店铺的访问令牌
   *
   * @param accessToken 访问令牌
   * @return 是否为新店铺的访问令牌
   */
  boolean saveOrUpdate(AccessToken accessToken);

  /**
   * 删除访问令牌
   *
   * @param shopId 店铺编号
   */
  void deleteByShopId(String shopId);

  /**
   * 查询访问令牌
   *
   * @param shopId 店铺编号
   * @return 访问令牌
   */
  AccessToken findByShopId(String shopId);

  /**
   * 查询所有的访问令牌
   *
   * @return 所有的访问令牌
   */
  List<AccessToken> findAll();

}
