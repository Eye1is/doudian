package me.gaigeshen.doudian.spring.boot.autoconfigure;

import me.gaigeshen.doudian.api.authorization.AccessTokenStore;
import me.gaigeshen.doudian.api.authorization.AccessTokenStoreImpl;
import me.gaigeshen.doudian.api.authorization.AccessTokenStoreJdbcImpl;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.sql.DataSource;

/**
 * @author gaigeshen
 */
@Configuration
public class DoudianJdbcAccessTokenStoreConfiguration {

  @Conditional({ DataSourceAvailableCondition.class, NonCustomAccessTokenCondition.class })
  @Bean
  public AccessTokenStore accessTokenStore(DataSource dataSource) {
    return AccessTokenStoreJdbcImpl.create(dataSource);
  }

  @Conditional({ NonCustomAccessTokenCondition.class })
  @Bean
  public AccessTokenStore accessTokenStore() {
    return AccessTokenStoreImpl.create();
  }

  static class NonCustomAccessTokenCondition extends AllNestedConditions {
    public NonCustomAccessTokenCondition() {
      super(ConfigurationPhase.PARSE_CONFIGURATION);
    }
    @ConditionalOnProperty(prefix = "doudian.authorization", name = "custom-access-token-store",
            havingValue = "false", matchIfMissing = true)
    static class ExplicitProperty {

    }
  }

  static class CustomAccessTokenCondition extends AllNestedConditions {
    public CustomAccessTokenCondition() {
      super(ConfigurationPhase.PARSE_CONFIGURATION);
    }
    @ConditionalOnProperty(prefix = "doudian.authorize", name = "data-source", havingValue = "true")
    static class ExplicitProperty {

    }
  }

  static class DataSourceAvailableCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
      ConditionMessage.Builder message = ConditionMessage.forCondition("DataSource");
      if (context.getRegistry().containsBeanDefinition("dataSource")) {
        return ConditionOutcome.match(message.foundExactly("supported DataSource"));
      }
      return ConditionOutcome.noMatch(message.didNotFind("supported DataSource").atAll());
    }
  }
}
