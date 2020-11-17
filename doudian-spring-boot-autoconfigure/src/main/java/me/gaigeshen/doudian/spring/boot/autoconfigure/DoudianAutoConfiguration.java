package me.gaigeshen.doudian.spring.boot.autoconfigure;

import me.gaigeshen.doudian.api.AppConfig;
import me.gaigeshen.doudian.api.authorization.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * @author gaigeshen
 */
@AutoConfigureAfter({ DataSourceAutoConfiguration.class }) // Maybe use jdbc data source
@ConditionalOnClass({ AccessToken.class }) // Include doudian lib ?
@EnableConfigurationProperties(DoudianProperties.class)
@Configuration
public class DoudianAutoConfiguration {

  private final DoudianProperties properties;

  private final DoudianProperties.Authorize authorize;

  public DoudianAutoConfiguration(DoudianProperties properties) {
    this.properties = properties;
    this.authorize = properties.getAuthorize();
  }

  @Bean
  public AuthorizationProcessor authorizationProcessor(AppConfig appConfig, AccessTokenManager accessTokenManager) {
    return new AuthorizationProcessorImpl(appConfig, accessTokenManager, authorize.getRedirectUri());
  }

  @Bean
  public AccessTokenManager accessTokenManager(AccessTokenStore accessTokenStore, AppConfig appConfig) {
    return new AccessTokenManagerImpl(accessTokenStore, appConfig);
  }

  @Bean
  public AccessTokenStore accessTokenStore(BeanFactory beanFactory) {
    // get data source by bean name from bean factory, if data source bean name provided
    // may be throws exception by bean factory if no such bean or bean type invalid
    String dataSource = authorize.getDataSource();
    if (Objects.nonNull(dataSource) && !dataSource.trim().equals("")) {
      return new AccessTokenStoreJdbcImpl(beanFactory.getBean(dataSource, DataSource.class));
    }
    // No data source bean name provided
    return new AccessTokenStoreImpl();
  }

  @Bean
  public AppConfig appConfig() {
    return new AppConfig(properties.getAppKey(), properties.getAppSecret());
  }

}
