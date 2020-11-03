package me.gaigeshen.doudian.api.authorization;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gaigeshen
 */
public class InMemoryAccessTokenStore implements AccessTokenStore {

  private final Map<String, AccessToken> internalStore = new ConcurrentHashMap<>();

  @Override
  public void saveOrUpdate(AccessToken accessToken) {
    if (Objects.nonNull(accessToken)) {
      internalStore.put(accessToken.getShopId(), accessToken);
    }
  }

  @Override
  public void deleteByShopId(String shopId) {
    if (StringUtils.isNotBlank(shopId)) {
      internalStore.remove(shopId);
    }
  }

  @Override
  public AccessToken findByShopId(String shopId) {
    if (StringUtils.isNotBlank(shopId)) {
      return internalStore.get(shopId);
    }
    return null;
  }

  @Override
  public List<AccessToken> findAll() {
    return new ArrayList<>(internalStore.values());
  }
}
