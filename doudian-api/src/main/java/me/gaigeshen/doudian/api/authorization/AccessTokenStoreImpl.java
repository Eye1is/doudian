package me.gaigeshen.doudian.api.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 访问令牌存储器默认的实现，采用简单的哈希存储
 *
 * @author gaigeshen
 */
public class AccessTokenStoreImpl implements AccessTokenStore {

  private final Map<String, AccessToken> internalStore = new ConcurrentHashMap<>();

  @Override
  public boolean saveOrUpdate(AccessToken accessToken) {
    if (Objects.isNull(accessToken)) {
      throw new IllegalArgumentException("Access token cannot be null");
    }
    return Objects.isNull(internalStore.put(accessToken.getShopId(), accessToken));
  }

  @Override
  public void deleteByShopId(String shopId) {
    if (Objects.isNull(shopId)) {
      throw new IllegalArgumentException("Shop id cannot be null");
    }
    internalStore.remove(shopId);
  }

  @Override
  public AccessToken findByShopId(String shopId) {
    if (Objects.isNull(shopId)) {
      throw new IllegalArgumentException("Shop id cannot be null");
    }
    return internalStore.get(shopId);
  }

  @Override
  public List<AccessToken> findAll() {
    return new ArrayList<>(internalStore.values());
  }
}
