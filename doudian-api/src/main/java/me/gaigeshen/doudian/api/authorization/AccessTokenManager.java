package me.gaigeshen.doudian.api.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.gaigeshen.doudian.api.AppConfig;
import me.gaigeshen.doudian.api.http.WebClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.gaigeshen.doudian.api.UriConstants.getAccessTokenRefreshUri;

/**
 * @author gaigeshen
 */
public class AccessTokenManager {

  private static final Logger logger = LoggerFactory.getLogger(AccessTokenManager.class);

  private final ScheduledExecutorService executorService;

  private final AccessTokenStore accessTokenStore;

  private final AppConfig appConfig;

  private final WebClient webClient;

  public AccessTokenManager(ScheduledExecutorService executorService, AccessTokenStore accessTokenStore, AppConfig appConfig, WebClient webClient) {
    this.executorService = executorService;
    this.accessTokenStore = accessTokenStore;
    this.appConfig = appConfig;
    this.webClient = webClient;
  }


  /**
   * @author gaigeshen
   */
  private class AccessTokenUpdateTask implements Runnable {

    private final AccessToken currentAccessToken;

    public AccessTokenUpdateTask(AccessToken currentAccessToken) {
      this.currentAccessToken = currentAccessToken;
    }

    public void start(long delaySeconds) {
      executorService.schedule(this, delaySeconds, TimeUnit.SECONDS);
    }

    public void start() {
      start(calcActionDelaySeconds(currentAccessToken));
    }

    public void startWithNewAccessToken(AccessToken accessToken) {
      new AccessTokenUpdateTask(accessToken).start();
    }

    @Override
    public void run() {
      boolean executeSuccess = false;
      try {
        AccessToken accessToken= getAccessTokenFromRemoteServer();
        executeSuccess = true;
        updateAccessToken(accessToken);
      } catch (Exception e) {
        if (!executeSuccess) {
          logger.warn("Cannot get access token from remote server, try again ten seconds later", e);
          start(10);
        }
      }
    }

    private void updateAccessToken(AccessToken newAccessToken) {
      try {
        accessTokenStore.saveOrUpdate(newAccessToken);
      } catch (Exception e) {
        throw new IllegalStateException("Cannot persist access token to store", e);
      }
    }

    private AccessToken getAccessTokenFromRemoteServer() {
      String uri = getAccessTokenRefreshUri(appConfig.getAppKey(), appConfig.getAppSecret(),
              currentAccessToken.getRefreshToken());

      String response = webClient.execute(new HttpGet(uri));


      try {
        return parseAccessToken(response);
      } catch (Exception e) {
        return null;
      }
    }

    private AccessToken getAccessTokenFromStore() {
      return accessTokenStore.findByShopId(currentAccessToken.getShopId());
    }

    private AccessToken parseAccessToken(String json) throws Exception {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(json);
      if (!jsonNode.isObject()) {
        throw new IllegalStateException("Cannot parse access token from: " + json);
      }
      JsonNode code = jsonNode.get("err_no");
      if (Objects.isNull(code) || code.asInt() != 0) {
        JsonNode message = code.get("message");
        throw new IllegalStateException(Objects.nonNull(message) ? message.asText() : "Cannot parse access token from: " + json);
      }
      AccessTokenBean bean = objectMapper.treeToValue(jsonNode, AccessTokenBean.class);
      if (Objects.isNull(bean) || Objects.isNull(bean.expiresIn)) {
        throw new IllegalStateException("Please check response from remote server");
      }
      if (StringUtils.isAnyBlank(bean.accessToken, bean.refreshToken, bean.shopId)) {
        throw new IllegalStateException("Missing access token, refresh token or shop id");
      }
      return AccessToken.builder()
              .setAccessToken(bean.accessToken)
              .setRefreshToken(bean.refreshToken)
              .setScope(bean.scope)
              .setShopId(bean.shopId)
              .setShopName(bean.shopName)
              .setExpiresIn(bean.expiresIn)
              .build();
    }

    private long calcActionDelaySeconds(AccessToken accessToken) {
      return (accessToken.getExpiresTimestamp() - 1800) - System.currentTimeMillis() / 1000;
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
  }

}
