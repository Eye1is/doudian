package me.gaigeshen.doudian.api.authorization;

import java.util.List;

/**
 * @author gaigeshen
 */
public class AccessTokenStoreJdbcImpl implements AccessTokenStore {

  private static final String TABLE_NAME = "dou_access_token";

  private static final String INSERT_SQL = "insert into " + TABLE_NAME + " (access_token, refresh_token, scope, shop_id, shop_name, expires_in, expires_timestamp, update_time) values (?, ?, ?, ?, ?, ?, ?, ?)";

  private static final String UPDATE_SQL = "update " + TABLE_NAME + " set access_token = ?, refresh_token = ?, scope = ?, shop_name = ?, expires_in = ?, expires_timestamp = ?, update_time = ? where shop_id = ?";

  private static final String FIND_BY_SHOP_ID_SQL = "select access_token, refresh_token, scope, shop_id, shop_name, expires_in, expires_timestamp, update_time from " + TABLE_NAME + " where shop_id = ?";

  private static final String FIND_ALL_SQL = "select access_token, refresh_token, scope, shop_id, shop_name, expires_in, expires_timestamp, update_time from " + TABLE_NAME;

  @Override
  public boolean saveOrUpdate(AccessToken accessToken) {
    return false;
  }

  @Override
  public void deleteByShopId(String shopId) {

  }

  @Override
  public AccessToken findByShopId(String shopId) {
    return null;
  }

  @Override
  public List<AccessToken> findAll() {
    return null;
  }
}
