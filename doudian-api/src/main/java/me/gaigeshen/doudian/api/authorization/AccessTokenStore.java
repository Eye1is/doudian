package me.gaigeshen.doudian.api.authorization;

import java.util.List;

/**
 * @author gaigeshen
 */
public interface AccessTokenStore {

  void saveOrUpdate(AccessToken accessToken);

  void deleteByShopId(String shopId);

  AccessToken findByShopId(String shopId);

  List<AccessToken> findAll();

}
