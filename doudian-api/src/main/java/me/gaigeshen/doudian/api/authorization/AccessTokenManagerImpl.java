package me.gaigeshen.doudian.api.authorization;

import me.gaigeshen.doudian.api.AppConfig;
import me.gaigeshen.doudian.api.http.WebClient;
import me.gaigeshen.doudian.api.http.WebClientException;
import me.gaigeshen.doudian.api.request.*;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
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

  private static final int STATUS_WAIT_START = 0;

  private static final int STATUS_STARTED = 1;

  private static final int STATUS_STOPED = 2;

  private static final int DEFAULT_THREAD_POOL_SIZE = 2;

  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(DEFAULT_THREAD_POOL_SIZE);

  private final WebClient webClient = WebClient.builder().build();

  private final ResponseParser<AccessToken> accessTokenResponseParser = new AccessTokenResponseParserImpl();

  private final AccessTokenStore accessTokenStore;

  private final AppConfig appConfig;

  private int status = STATUS_WAIT_START;

  /**
   * 创建访问令牌管理器
   *
   * @param accessTokenStore 访问令牌存储器不能为空
   * @param appConfig 应用配置不能为空
   */
  public AccessTokenManagerImpl(AccessTokenStore accessTokenStore, AppConfig appConfig) {
    if (Objects.isNull(accessTokenStore) || Objects.isNull(appConfig)) {
      throw new IllegalArgumentException("accessTokenStore and appConfig cannot be null");
    }
    this.accessTokenStore = new AccessTokenStoreCacheWrapper(accessTokenStore);
    this.appConfig = appConfig;
  }

  @Override
  public AccessToken saveAccessToken(AccessToken accessToken) throws AccessTokenManagerException {
    try {
      if (accessTokenStore.saveOrUpdate(accessToken)) {
        new AccessTokenUpdateTask(accessToken).start();
      }
    } catch (AccessTokenStoreException e) {
      throw new AccessTokenManagerException("Could not save or update access token because store exception:: ", e);
    }
    return accessToken;
  }

  @Override
  public AccessToken findAccessToken(String shopId) throws AccessTokenManagerException {
    try {
      return accessTokenStore.findByShopId(shopId);
    } catch (AccessTokenStoreException e) {
      throw new AccessTokenManagerException("Could not find access token because store exception:: ", e);
    }
  }

  @Override
  public List<AccessToken> findAccessTokens() throws AccessTokenManagerException {
    try {
      return accessTokenStore.findAll();
    } catch (AccessTokenStoreException e) {
      throw new AccessTokenManagerException("Could not find all access tokens because store exception:: ", e);
    }
  }

  @Override
  public void deleteAccessToken(String shopId) throws AccessTokenManagerException {
    try {
      accessTokenStore.deleteByShopId(shopId);
    } catch (AccessTokenStoreException e) {
      throw new AccessTokenManagerException("Could not delete access token because store exception:: ", e);
    }
  }

  @Override
  public synchronized void startup() throws AccessTokenManagerException {
    if (status > STATUS_WAIT_START) {
      logger.warn("Could not startup because this access token manager has started::");
      return;
    }
    status = STATUS_STARTED;
    for (AccessToken accessToken : findAccessTokens()) {
      new AccessTokenUpdateTask(accessToken).start();
    }
  }

  @Override
  public synchronized void shutdown() throws AccessTokenManagerException {
    if (status != STATUS_STARTED) {
      logger.warn("Could not shutdown because access token manager has not started or stoped::");
      return;
    }
    status = STATUS_STOPED;
    executorService.shutdownNow();
    try {
      executorService.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new AccessTokenManagerException("Current thread interrupted while shutting down access token manager", e);
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
   * @throws AccessTokenManagerException 查询该店铺的访问令牌时发生异常
   */
  private boolean hasShopAccessToken(String shopId) throws AccessTokenManagerException {
    return Objects.nonNull(findAccessToken(shopId));
  }

  /**
   * 获取远程的访问令牌，通过刷新令牌获取
   *
   * @param currentAccessToken 当前的访问令牌
   * @return 获取到的访问令牌
   * @throws WebClientException 请求远程数据失败
   * @throws ResponseParseException 转换请求结果失败
   * @throws RequestResultException 请求执行结果异常
   */
  private AccessToken getRemoteAccessToken(AccessToken currentAccessToken) throws WebClientException, ResponseParseException, RequestResultException {
    String refreshToken = currentAccessToken.getRefreshToken();
    HttpGet req = new HttpGet(getAccessTokenRefreshUri(appConfig.getAppKey(), appConfig.getAppSecret(), refreshToken));
    String rawString = webClient.execute(req);
    return accessTokenResponseParser.parse(DefaultResponse.create(rawString));
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
      try {
        executorService.schedule(this, delaySeconds, TimeUnit.SECONDS);
      } catch (RejectedExecutionException e) {
        // This cannot be happening
        logger.warn("Could not schedule access token update task:: " + currentAccessToken.getShopName());
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
      logger.info("Run access token update task:: " + currentAccessToken.getShopName());
      AccessToken remoteAccessToken;
      try {
        if (!hasShopAccessToken(currentAccessToken.getShopId())) {
          return;
        }
        remoteAccessToken = getRemoteAccessToken(currentAccessToken);
      } catch (Exception e) {
        if (e instanceof WebClientException) {
          logger.warn("Could not get remote access token because web client exception, try again 10 seconds later:: "
                  + currentAccessToken.getShopName(), e);
          start(10);
        } else {
          logger.warn("Could not get remote access token, exit this access token update task:: "
                  + currentAccessToken.getShopName(), e);
        }
        return;
      }
      startWithNewAccessToken(remoteAccessToken);

      try {
        accessTokenStore.saveOrUpdate(remoteAccessToken);
      } catch (Exception e) {
        // 执行更新访问令牌的时候发生异常，需要重试吗？
        // 此时的异常可能是本地存储状态发生异常，往后肯定会修复正常，后续更新操作肯定会同步到本地存储
        // 建议访问令牌存储器对这类情况做相应的处理
        logger.warn("Could not update access token to store:: " + currentAccessToken.getShopName(), e);
      }
    }
  }

  /**
   * 访问令牌存储器缓存包装，对缓存中的操作不会抛出异常，所有的异常均来自原始存储器
   *
   * @author gaigeshen
   */
  private class AccessTokenStoreCacheWrapper implements AccessTokenStore {

    private final AccessTokenStore internalStore = new AccessTokenStoreImpl();

    private final AccessTokenStore originStore;

    public AccessTokenStoreCacheWrapper(AccessTokenStore originStore) {
      this.originStore = originStore;
    }

    // 先保存到缓存中，再保存到原始存储器
    @Override
    public boolean saveOrUpdate(AccessToken accessToken) throws AccessTokenStoreException {
      internalStore.saveOrUpdate(accessToken);
      return originStore.saveOrUpdate(accessToken);
    }

    // 先删除缓存中的，再删除原始的
    @Override
    public void deleteByShopId(String shopId) throws AccessTokenStoreException {
      internalStore.deleteByShopId(shopId);
      originStore.deleteByShopId(shopId);
    }

    // 直接查询缓存中的，缓存中不存在再去查询原始的
    @Override
    public AccessToken findByShopId(String shopId) throws AccessTokenStoreException {
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
    public List<AccessToken> findAll() throws AccessTokenStoreException {
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
