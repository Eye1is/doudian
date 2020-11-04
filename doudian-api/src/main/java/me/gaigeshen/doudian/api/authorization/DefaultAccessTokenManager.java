package me.gaigeshen.doudian.api.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.gaigeshen.doudian.api.AppConfig;
import me.gaigeshen.doudian.api.http.WebClient;
import me.gaigeshen.doudian.api.request.DefaultResponse;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.gaigeshen.doudian.api.Constants.getAccessTokenRefreshUri;

/**
 * 访问令牌管理器，负责维护令牌的更新，获取令牌也可以通过此类对象
 *
 * @author gaigeshen
 */
public class DefaultAccessTokenManager implements AccessTokenManager {

  private static final Logger logger = LoggerFactory.getLogger(DefaultAccessTokenManager.class);

  private static final int DEFAULT_THREAD_POOL_SIZE = 1;

  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(DEFAULT_THREAD_POOL_SIZE);

  private final WebClient webClient = WebClient.builder().build();

  private final AccessTokenStore accessTokenStore;

  private final AppConfig appConfig;

  private DefaultAccessTokenManager(AccessTokenStore accessTokenStore, AppConfig appConfig) {
    this.accessTokenStore = accessTokenStore;
    this.appConfig = appConfig;
  }

  public static DefaultAccessTokenManager create(AccessTokenStore accessTokenStore, AppConfig appConfig) {
    return new DefaultAccessTokenManager(accessTokenStore, appConfig);
  }

  @Override
  public void init() {
    for (AccessToken accessToken : accessTokenStore.findAll()) {
      new AccessTokenUpdateTask(accessToken).start();
    }
  }

  @Override
  public void persistAccessToken(AccessToken accessToken) {
    accessTokenStore.saveOrUpdate(accessToken);
    new AccessTokenUpdateTask(accessToken).start();
  }

  @Override
  public void deleteAccessToken(String shopId) {
    accessTokenStore.deleteByShopId(shopId);
  }

  @Override
  public AccessToken getAccessToken(String shopId) {
    return accessTokenStore.findByShopId(shopId);
  }

  @Override
  public void close() throws IOException {
    executorService.shutdownNow();
    try {
      executorService.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      // 等待线程池终止的过程中被打断
    }

    webClient.close();
  }

  private AccessToken getLocalAccessToken(AccessToken currentAccessToken) {
    return getAccessToken(currentAccessToken.getShopId());
  }

  private AccessToken getRemoteAccessToken(AccessToken currentAccessToken) {
    String refreshToken = currentAccessToken.getRefreshToken();
    HttpGet req = new HttpGet(getAccessTokenRefreshUri(appConfig.getAppKey(), appConfig.getAppSecret(), refreshToken));
    String rawString = webClient.execute(req);
    DefaultResponse response = DefaultResponse.create(rawString);
    if (!response.isSuccess()) {
      throw new IllegalStateException(response.getMessage());
    }
    AccessTokenBean accessTokenBean = response.parseObject(AccessTokenBean.class);
    return AccessToken.builder()
            .setAccessToken(accessTokenBean.accessToken)
            .setRefreshToken(accessTokenBean.refreshToken)
            .setScope(accessTokenBean.scope)
            .setShopId(accessTokenBean.shopId)
            .setShopName(accessTokenBean.shopName)
            .setExpiresIn(accessTokenBean.expiresIn)
            // 返回的是过期时间段单位秒，计算过期时间点单位秒
            .setExpiresTimestamp(System.currentTimeMillis() / 1000 + accessTokenBean.expiresIn)
            .build();
  }

  /**
   * 访问令牌更新任务
   *
   * @author gaigeshen
   */
  private class AccessTokenUpdateTask implements Runnable {

    /** 当前的访问令牌 */
    private final AccessToken currentAccessToken;

    public AccessTokenUpdateTask(AccessToken currentAccessToken) {
      this.currentAccessToken = currentAccessToken;
    }

    /** 开始调度任务，但是可以指定任务的执行延期时间 */
    public void start(long delaySeconds) {
      logger.info("Start access token update task:: shop " + currentAccessToken.getShopName());
      try {
        executorService.schedule(this, delaySeconds, TimeUnit.SECONDS);
      } catch (RejectedExecutionException e) {
        // 线程池被关闭，无法调度新的任务
        logger.warn("Could not start task, this manager closed:: shop " + currentAccessToken.getShopName());
      }
    }
    /** 开始调度任务，使用当前的访问令牌计算任务的执行延期时间 */
    public void start() {
      start(calcActionDelaySeconds(currentAccessToken));
    }
    /** 开始调度任务，使用新的访问令牌，并且使用该访问令牌计算任务的执行延期时间 */
    public void startWithNewAccessToken(AccessToken accessToken) {
      new AccessTokenUpdateTask(accessToken).start();
    }

    private long calcActionDelaySeconds(AccessToken accessToken) {
      return (accessToken.getExpiresTimestamp() - 1800) - System.currentTimeMillis() / 1000;
    }

    @Override
    public void run() {
      // 确保当前的访问令牌是存在的，由于该店铺在本地存储已被移除访问令牌，则放弃更新该访问令牌
      if (Objects.isNull(getLocalAccessToken(currentAccessToken))) {
        return;
      }
      // 开始获取远程最新的访问令牌
      AccessToken remoteAccessToken;
      try {
        remoteAccessToken = getRemoteAccessToken(currentAccessToken);
      } catch (Exception e) {
        // 1. 网络请求失败，稍后再试
        // 2. 该店铺被移除授权，稍后再试
        // 3. 转换响应结果失败，不建议再试，发生可能性低
        logger.warn("Could not get remote access token, try again 10 seconds later:: shop "
                + currentAccessToken.getShopName(), e);
        start(10);
        return;
      }
      startWithNewAccessToken(remoteAccessToken);

      try {
        accessTokenStore.saveOrUpdate(remoteAccessToken);
      } catch (Exception e) {
        // 执行更新访问令牌的时候发生异常，需要重试吗？
        // 此时的异常可能是本地存储状态发生异常，往后肯定会修复正常，后续更新操作肯定会同步到本地存储
        // 如果我们的存储器具有本地缓存的功能，那么是可以容忍这种情况的发生，选择不进行重试，需要将此访问令牌管理器作为首选获取访问令牌的渠道
        // 本类已实现本地缓存的功能
        logger.warn("Could not update access token to store:: shop " + currentAccessToken.getShopName(), e);
      }
    }
  }

  /**
   * @author gaigeshen
   */
  public static class AccessTokenBean {

    @JsonProperty("access_token") private String accessToken;

    @JsonProperty("refresh_token") private String refreshToken;

    @JsonProperty("scope") private String scope;

    @JsonProperty("shop_id") private String shopId;

    @JsonProperty("shop_name") private String shopName;

    @JsonProperty("expires_in") private Long expiresIn;
  }

}
