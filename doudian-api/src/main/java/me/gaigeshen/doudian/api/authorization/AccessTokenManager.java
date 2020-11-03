package me.gaigeshen.doudian.api.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.gaigeshen.doudian.api.AppConfig;
import me.gaigeshen.doudian.api.http.JacksonJsonBeanResponseHandler;
import me.gaigeshen.doudian.api.http.WebClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.gaigeshen.doudian.api.UriConstants.getAccessTokenRefreshUri;

/**
 * @author gaigeshen
 */
public class AccessTokenManager {

  private static final Logger logger = LoggerFactory.getLogger(AccessTokenManager.class);

  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);

  private final AccessTokenStore accessTokenStore;

  private final AppConfig appConfig;

  private final WebClient webClient;

  public AccessTokenManager(AccessTokenStore accessTokenStore, AppConfig appConfig, WebClient webClient) {
    this.accessTokenStore = accessTokenStore;
    this.appConfig = appConfig;
    this.webClient = webClient;
  }

  public AccessToken getAccessToken(String shopId) {
    return accessTokenStore.findByShopId(shopId);
  }

  /**
   * @author gaigeshen
   */
  private class AccessTokenUpdateTask implements Runnable {

    private final AccessToken oldAccessToken; // 当前的访问令牌

    private final long actionTimestamp; // 任务执行时间单位秒

    public AccessTokenUpdateTask(AccessToken oldAccessToken) {
      this.oldAccessToken = oldAccessToken;
      this.actionTimestamp = calcDefaultActionTimestamp(oldAccessToken);
    }

    public AccessTokenUpdateTask(AccessToken oldAccessToken, long actionTimestamp) {
      this.oldAccessToken = oldAccessToken;
      this.actionTimestamp = actionTimestamp;
    }

    public void start() {
      executorService.schedule(this, actionTimestamp, TimeUnit.SECONDS);
    }

    public void startLater(long seconds) {
      new AccessTokenUpdateTask(oldAccessToken, System.currentTimeMillis() / 1000 + seconds);
    }

    public void startWithNewAccessToken(AccessToken accessToken) {
      new AccessTokenUpdateTask(accessToken).start();
    }

    @Override
    public void run() {
      // 获取并检查新的访问令牌
      AccessToken accessToken = checkAccessTokenBean(requestNewAccessToken());
      // 如果该访问令牌不为空且更新访问令牌成功，则以该访问令牌开启新的任务
      if (Objects.nonNull(accessToken) && updateAccessToken(accessToken)) {
        startWithNewAccessToken(accessToken);
      } else {
        // 没有成功获取到正确的访问令牌，或者更新访问令牌失败，五秒后重试
        logger.warn("Update access token five seconds later because update failed now");
        startLater(5);
      }
    }

    private long calcDefaultActionTimestamp(AccessToken accessToken) {
      return (accessToken.getExpiresTimestamp() - 1800) - System.currentTimeMillis() / 1000;
    }

    private boolean updateAccessToken(AccessToken accessToken) {
      try {
        accessTokenStore.saveOrUpdate(accessToken);
        return true;
      } catch (Exception e) {
        return false;
      }
    }

    private AccessToken checkAccessTokenBean(AccessTokenBean accessTokenBean) {
      if (Objects.isNull(accessTokenBean)) {
        return null;
      }
      if (Objects.isNull(accessTokenBean.getExpiresIn())) {
        return null;
      }
      if (StringUtils.isAnyBlank(accessTokenBean.getAccessToken(), accessTokenBean.getRefreshToken(),
              accessTokenBean.getShopId())) {
        return null;
      }
      Long expiresIn = accessTokenBean.getExpiresIn();
      long expiresTimestamp = (System.currentTimeMillis() + expiresIn * 1000) / 1000;
      return AccessToken.builder()
              .setAccessToken(accessTokenBean.getAccessToken())
              .setRefreshToken(accessTokenBean.getRefreshToken())
              .setScope(accessTokenBean.getScope())
              .setShopId(accessTokenBean.getShopId())
              .setShopName(accessTokenBean.getShopName())
              .setExpiresIn(expiresIn)
              .setExpiresTimestamp(expiresTimestamp)
              .build();
    }

    private AccessTokenBean requestNewAccessToken() {
      HttpGet get = new HttpGet(getAccessTokenRefreshUri(appConfig.getAppKey(), appConfig.getAppSecret(),
              oldAccessToken.getRefreshToken()));
      AccessTokenBean accessTokenBean = null;
      try {
        accessTokenBean = webClient.execute(get, new JacksonJsonBeanResponseHandler<>(AccessTokenBean.class));
      } catch (Exception e) {
        logger.warn("Could not request new access token", e);
      }
      return accessTokenBean;
    }
  }

  /**
   * @author gaigeshen
   */
  public static class AccessTokenBean {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("shop_id")
    private String shopId;

    @JsonProperty("shop_name")
    private String shopName;

    @JsonProperty("expires_in")
    private Long expiresIn;

    public String getAccessToken() { return accessToken; }

    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }

    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getScope() { return scope; }

    public void setScope(String scope) { this.scope = scope; }

    public String getShopId() { return shopId; }

    public void setShopId(String shopId) { this.shopId = shopId; }

    public String getShopName() { return shopName; }

    public void setShopName(String shopName) { this.shopName = shopName; }

    public Long getExpiresIn() { return expiresIn; }

    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
  }

}
