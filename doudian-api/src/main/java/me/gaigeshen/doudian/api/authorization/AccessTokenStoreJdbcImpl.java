package me.gaigeshen.doudian.api.authorization;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * 此访问令牌管理器基于数据库的数据源实现
 *
 * @author gaigeshen
 */
public class AccessTokenStoreJdbcImpl implements AccessTokenStore {

  private static final String TABLE_NAME = "dou_access_token";

  private static final String INSERT_SQL = "insert into " + TABLE_NAME + " (access_token, refresh_token, scope, shop_id, shop_name, expires_in, expires_timestamp, update_time) values (?, ?, ?, ?, ?, ?, ?, ?)";

  private static final String UPDATE_SQL = "update " + TABLE_NAME + " set access_token = ?, refresh_token = ?, scope = ?, shop_name = ?, expires_in = ?, expires_timestamp = ?, update_time = ? where shop_id = ?";

  private static final String DELETE_BY_SHOP_ID_SQL = "delete from " + TABLE_NAME + " where shop_id = ?";

  private static final String FIND_BY_SHOP_ID_SQL = "select access_token, refresh_token, scope, shop_id, shop_name, expires_in, expires_timestamp, update_time from " + TABLE_NAME + " where shop_id = ?";

  private static final String FIND_ALL_SQL = "select access_token, refresh_token, scope, shop_id, shop_name, expires_in, expires_timestamp, update_time from " + TABLE_NAME;

  // 用于转换单条访问令牌数据库记录
  private static final ResultSetHandler<Map<String, Object>> DATABASE_RESULT_HANDLER = new MapHandler();

  // 用于转换多条访问令牌数据库记录
  private static final ResultSetHandler<List<Map<String, Object>>> DATABASE_RESULTS_HANDLER = new MapListHandler();

  private final DataSource dataSource;

  private final QueryRunner queryRunner;

  private AccessTokenStoreJdbcImpl(DataSource dataSource) {
    Validate.isTrue(Objects.nonNull(dataSource), "Data source cannot be null");
    this.dataSource = dataSource;
    this.queryRunner = new QueryRunner(dataSource);
  }

  /**
   * 创建访问令牌存储器
   *
   * @param dataSource 数据源
   * @return 访问令牌存储器
   */
  public static AccessTokenStoreJdbcImpl create(DataSource dataSource) {
    return new AccessTokenStoreJdbcImpl(dataSource);
  }

  @Override
  public boolean saveOrUpdate(AccessToken accessToken) {
    Connection connection = prepareTransactionalConnection();
    try {
      // 先执行更新操作，如果更新成功则说明该店铺已经存在访问令牌
      int result = queryRunner.update(connection, UPDATE_SQL, accessToken.getAccessToken(), accessToken.getRefreshToken(), accessToken.getScope(), accessToken.getShopName(), accessToken.getExpiresIn(), accessToken.getExpiresTimestamp(), new Date(), accessToken.getShopId());
      if (result > 0) {
        return false;
      }
      // 为新店铺增加访问令牌
      queryRunner.update(connection, INSERT_SQL, accessToken.getAccessToken(), accessToken.getRefreshToken(), accessToken.getScope(), accessToken.getShopId(), accessToken.getShopName(), accessToken.getExpiresIn(), accessToken.getExpiresTimestamp(), new Date());
      connection.commit();
    } catch (SQLException e) {
      // 操作数据库过程中发生异常，本次所有操作失败，抛出异常
      DbUtils.rollbackAndCloseQuietly(connection);
      throw new IllegalStateException("Could not save or update access token:: shop id " + accessToken.getShopId() + ", shop name " + accessToken.getShopName(), e);
    } finally {
      // 确保数据库连接被关闭
      DbUtils.closeQuietly(connection);
    }
    // 所有数据库的操作成功，增加了新店铺的访问令牌
    return true;
  }

  @Override
  public void deleteByShopId(String shopId) {
    if (StringUtils.isBlank(shopId)) {
      return;
    }
    try {
      queryRunner.update(DELETE_BY_SHOP_ID_SQL, shopId);
    } catch (SQLException e) {
      throw new IllegalStateException("Could not delete access token:: shop id " + shopId, e);
    }
  }

  @Override
  public AccessToken findByShopId(String shopId) {
    if (StringUtils.isBlank(shopId)) {
      return null;
    }
    Map<String, Object> result;
    try {
      result = queryRunner.query(FIND_BY_SHOP_ID_SQL, DATABASE_RESULT_HANDLER, shopId);
    } catch (SQLException e) {
      throw new IllegalStateException("Could not find access token:: shop id " + shopId, e);
    }
    if (Objects.isNull(result)) {
      return null;
    }
    return parseAccessToken(result);
  }

  @Override
  public List<AccessToken> findAll() {
    List<AccessToken> accessTokens = new ArrayList<>();
    List<Map<String, Object>> results;
    try {
      results = queryRunner.query(FIND_ALL_SQL, DATABASE_RESULTS_HANDLER);
    } catch (SQLException e) {
      throw new IllegalStateException("Could not find all access tokens:: ", e);
    }
    if (results.isEmpty()) {
      return accessTokens;
    }
    for (Map<String, Object> result : results) {
      accessTokens.add(parseAccessToken(result));
    }
    return accessTokens;
  }

  private AccessToken parseAccessToken(Map<String, Object> databaseResult) {
    try {
      return AccessToken.builder()
              .setAccessToken((String) databaseResult.get("access_token"))
              .setRefreshToken((String) databaseResult.get("refresh_token"))
              .setScope((String) databaseResult.get("scope"))
              .setShopId((String) databaseResult.get("shopId"))
              .setShopName((String) databaseResult.get("shop_name"))
              .setExpiresIn((Long) databaseResult.get("expires_in"))
              .setExpiresTimestamp((Long) databaseResult.get("expires_timestamp"))
              .setUpdateTime((Date) databaseResult.get("update_time"))
              .build();
    } catch (Exception e) {
      throw new IllegalStateException("Could not parse to access token object:: database result " + databaseResult);
    }
  }

  private Connection prepareTransactionalConnection() {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(true);
      return connection;
    } catch (SQLException e) {
      DbUtils.closeQuietly(connection);
      throw new IllegalStateException("Could not get database connection:: data source " + dataSource);
    }
  }

}
