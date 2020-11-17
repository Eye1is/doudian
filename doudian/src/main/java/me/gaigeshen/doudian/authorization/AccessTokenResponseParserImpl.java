package me.gaigeshen.doudian.api.authorization;

import me.gaigeshen.doudian.api.request.RequestResultException;
import me.gaigeshen.doudian.api.request.Response;
import me.gaigeshen.doudian.api.request.ResponseParseException;
import me.gaigeshen.doudian.api.request.ResponseParser;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 访问令牌请求响应结果转换器，用于将请求响应结果转换为访问令牌
 *
 * @author gaigeshen
 */
public class AccessTokenResponseParserImpl implements ResponseParser<AccessToken> {
  @Override
  public AccessToken parse(Response resp) throws ResponseParseException, RequestResultException {
    if (Objects.isNull(resp)) {
      throw new IllegalArgumentException("Response is required");
    }
    if (resp.isFailed()) {
      throw new RequestResultException("Could not parse access token from failed response, " + resp.getMessage() + "::");
    }
    // 返回的是业务数据部分
    Map<String, Object> accessTokenData = resp.parseMapping();
    // 取访问令牌的各个字段
    String accessToken = MapUtils.getString(accessTokenData, "access_token");
    String refreshToken = MapUtils.getString(accessTokenData, "refresh_token");
    String scope = MapUtils.getString(accessTokenData, "scope");
    String shopId = MapUtils.getString(accessTokenData, "shop_id");
    String shopName = MapUtils.getString(accessTokenData, "shop_name");
    Long expiresIn = MapUtils.getLong(accessTokenData, "expires_in");
    // 访问令牌必需的数据进行判断
    if (StringUtils.isAnyBlank(accessToken, refreshToken, shopId) || Objects.isNull(expiresIn)) {
      throw new IllegalStateException("Could not parse valid access token from response:: " + resp.getRawString());
    }

    return AccessToken.builder()
            .setAccessToken(accessToken).setRefreshToken(refreshToken)
            .setScope(scope).setShopId(shopId).setShopName(shopName)
            .setExpiresIn(expiresIn).setExpiresTimestamp(System.currentTimeMillis() / 1000 + expiresIn)
            .setUpdateTime(new Date())
            .build();
  }
}
