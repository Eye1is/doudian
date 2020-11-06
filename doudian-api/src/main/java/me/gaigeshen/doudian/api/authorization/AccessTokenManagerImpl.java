package me.gaigeshen.doudian.api.authorization;

import me.gaigeshen.doudian.api.AppConfig;
import me.gaigeshen.doudian.api.http.WebClient;
import me.gaigeshen.doudian.api.request.DefaultResponse;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.gaigeshen.doudian.api.Constants.getAccessTokenRefreshUri;

/**
 * 采用线程池的实现来用于更新每个访问令牌，每个访问令牌被安排为单独的线程任务被调度，前提是调用了启动方法
 *
 * @author gaigeshen
 */
public class AccessTokenManagerImpl implements AccessTokenManager {

  private static final Logger logger = LoggerFactory.getLogger(AccessTokenManagerImpl.class);

  private static final int DEFAULT_THREAD_POOL_SIZE = 1;

  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(DEFAULT_THREAD_POOL_SIZE);

  private final WebClient webClient = WebClient.builder().build();

  private final AccessTokenStore accessTokenStore;

  private final AppConfig appConfig;

  private AccessTokenManagerImpl(AccessTokenStore accessTokenStore, AppConfig appConfig) {
    Validate.isTrue(Objects.nonNull(accessTokenStore), "accessTokenStore cannot be null");
    Validate.isTrue(Objects.nonNull(appConfig), "appConfig can not be null");
    this.accessTokenStore = new AccessTokenStoreCacheWrapper(accessTokenStore);
    this.appConfig = appConfig;
  }

  /**
   * 创建访问令牌管理器
   *
   * @param accessTokenStore 访问令牌存储器不能为空
   * @param appConfig 应用配置不能为空
   * @return 访问令牌管理器
   */
  public static AccessTokenManagerImpl create(AccessTokenStore accessTokenStore, AppConfig appConfig) {
    return new AccessTokenManagerImpl(accessTokenStore, appConfig);
  }

  @Override
  public AccessTokenStore getAccessTokenStore() {
    return accessTokenStore;
  }

  @Override
  public AccessToken saveAccessToken(AccessToken accessToken) {
    if (accessTokenStore.saveOrUpdate(accessToken)) {
      new AccessTokenUpdateTask(accessToken).start();
    }
    return accessToken;
  }

  @Override
  public AccessToken findAccessToken(String shopId) {
    return accessTokenStore.findByShopId(shopId);
  }

  @Override
  public List<AccessToken> findAccessTokens() {
    return accessTokenStore.findAll();
  }

  @Override
  public void deleteAccessToken(String shopId) {
    accessTokenStore.deleteByShopId(shopId);
  }

  @Override
  public void startup() {
    startAllUpdateTasks();
  }

  /**
   * 从访问令牌存储器中获取所有的访问令牌，然后为每个访问令牌开启更新任务
   */
  private void startAllUpdateTasks() {
    for (AccessToken accessToken : accessTokenStore.findAll()) {
      new AccessTokenUpdateTask(accessToken).start();
    }
  }

  @Override
  public void shutdown() {
    executorService.shutdownNow();
    try {
      executorService.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException ignored) {
    }
    try {
      webClient.close();
    } catch (IOException ignored) {
    }
  }

  /**
   * 返回是否存在指定店铺的访问令牌
   *
   * @param shopId 店铺编号
   * @return 是否存在该店铺的访问令牌
   */
  private boolean hasShopAccessToken(String shopId) {
    return Objects.nonNull(findAccessToken(shopId));
  }

  /**
   * 获取远程的访问令牌，通过刷新令牌获取
   *
   * @param currentAccessToken 当前的访问令牌
   * @return 获取到的访问令牌
   */
  private AccessToken getRemoteAccessToken(AccessToken currentAccessToken) {
    String refreshToken = currentAccessToken.getRefreshToken();
    HttpGet req = new HttpGet(getAccessTokenRefreshUri(appConfig.getAppKey(), appConfig.getAppSecret(), refreshToken));
    String rawString = webClient.execute(req);
    DefaultResponse response = DefaultResponse.create(rawString);
    if (!response.isSuccess()) {
      throw new IllegalStateException("Could not get remote access token, " + response.getMessage() + ":: shop "
              + currentAccessToken.getShopName());
    }
    Map<String, Object> accessTokenData = response.parseMapping();
    String accessToken = MapUtils.getString(accessTokenData, "access_token");
    String newRefreshToken = MapUtils.getString(accessTokenData, "refresh_token");
    String scope = MapUtils.getString(accessTokenData, "scope");
    String shopId = MapUtils.getString(accessTokenData, "shop_id");
    String shopName = MapUtils.getString(accessTokenData, "shop_name");
    Long expiresIn = MapUtils.getLong(accessTokenData, "expires_in");
    if (StringUtils.isAnyBlank(accessToken, refreshToken, shopId) || Objects.isNull(expiresIn)) {
      throw new IllegalStateException("Could not get valid remote access token:: shop " + currentAccessToken.getShopName());
    }
    return AccessToken.builder()
            .setAccessToken(accessToken).setRefreshToken(newRefreshToken)
            .setScope(scope).setShopId(shopId).setShopName(shopName)
            .setExpiresIn(expiresIn).setExpiresTimestamp(System.currentTimeMillis() / 1000 + expiresIn)
            .setUpdateTime(new Date())
            .build();
  }

  /**
   * 访问令牌更新任务
   *
   * @author gaigeshen
   */
  private class AccessTokenUpdateTask implements Runnable {

    private final AccessToken currentAccessToken;

    public AccessTokenUpdateTask(AccessToken currentAccessToken) {
      this.currentAccessToken = currentAccessToken;
    }

    public void start(long delaySeconds) {
      logger.info("Start access token update task with " + delaySeconds + " seconds:: shop "
              + currentAccessToken.getShopName());
      try {
        executorService.schedule(this, delaySeconds, TimeUnit.SECONDS);
      } catch (RejectedExecutionException e) {
        logger.warn("Could not start task, this access token manager closed:: shop " + currentAccessToken.getShopName());
      }
    }
    public void start() {
      start(calcActionDelaySeconds(currentAccessToken));
    }
    public void startWithNewAccessToken(AccessToken accessToken) {
      new AccessTokenUpdateTask(accessToken).start();
    }

    private long calcActionDelaySeconds(AccessToken accessToken) {
      return (accessToken.getExpiresTimestamp() - 1800) - System.currentTimeMillis() / 1000;
    }

    @Override
    public void run() {
      if (!hasShopAccessToken(currentAccessToken.getShopId())) {
        return;
      }
      AccessToken remoteAccessToken;
      try {
        remoteAccessToken = getRemoteAccessToken(currentAccessToken);
      } catch (Exception e) {
        // 网络请求失败或者由于远程服务器当前无法返回新的访问令牌，稍后再试
        // 处理响应结果失败，不建议再试，基本不会发生这种情况
        logger.warn("Could not get remote access token, try again 10 seconds later:: shop "
                + currentAccessToken.getShopName(), e);
        start(10);
        return;
      }
      // 如果此访问令牌管理器被关闭的情况，此方法不会开启新的任务
      startWithNewAccessToken(remoteAccessToken);

      try {
        accessTokenStore.saveOrUpdate(remoteAccessToken);
      } catch (Exception e) {
        // 执行更新访问令牌的时候发生异常，需要重试吗？
        // 此时的异常可能是本地存储状态发生异常，往后肯定会修复正常，后续更新操作肯定会同步到本地存储
        // 建议访问令牌存储器对这类情况做相应的处理
        logger.warn("Could not update access token to store:: shop " + currentAccessToken.getShopName(), e);
      }
    }
  }

  /**
   * 访问令牌存储器缓存包装，对缓存中的操作不会抛出异常，所有的异常均来自原始存储器
   *
   * @author gaigeshen
   */
  private class AccessTokenStoreCacheWrapper implements AccessTokenStore {

    private final AccessTokenStore internalStore = AccessTokenStoreImpl.create();

    private final AccessTokenStore originStore;

    private AccessTokenStoreCacheWrapper(AccessTokenStore originStore) {
      this.originStore = originStore;
    }

    // 先保存到缓存中，再保存到原始存储器
    @Override
    public boolean saveOrUpdate(AccessToken accessToken) {
      internalStore.saveOrUpdate(accessToken);
      return originStore.saveOrUpdate(accessToken);
    }

    // 先删除缓存中的，再删除原始的
    @Override
    public void deleteByShopId(String shopId) {
      internalStore.deleteByShopId(shopId);
      originStore.deleteByShopId(shopId);
    }

    // 直接查询缓存中的，缓存中不存在再去查询原始的
    @Override
    public AccessToken findByShopId(String shopId) {
      AccessToken accessToken = internalStore.findByShopId(shopId);
      if (Objects.nonNull(accessToken)) {
        return accessToken;
      }
      AccessToken accessTokenFromOrigin = originStore.findByShopId(shopId);
      if (Objects.nonNull(accessTokenFromOrigin)) {
        internalStore.saveOrUpdate(accessTokenFromOrigin);
      }
      return accessTokenFromOrigin;
    }

    // 直接查询缓存中的，缓存中不存在再去查询原始的，没有考虑缓存和原始数据非空不同集合的情况
    @Override
    public List<AccessToken> findAll() {
      List<AccessToken> accessTokens = internalStore.findAll();
      if (!accessTokens.isEmpty()) {
        return accessTokens;
      }
      List<AccessToken> accessTokensFromOrigin = originStore.findAll();
      if (!accessTokensFromOrigin.isEmpty()) {
        for (AccessToken accessTokenFromOrigin : accessTokensFromOrigin) {
          internalStore.saveOrUpdate(accessTokenFromOrigin);
        }
      }
      return accessTokensFromOrigin;
    }
  }
}
