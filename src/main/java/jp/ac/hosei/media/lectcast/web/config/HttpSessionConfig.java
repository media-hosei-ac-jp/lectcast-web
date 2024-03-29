package jp.ac.hosei.media.lectcast.web.config;

import java.time.Duration;
import jp.ac.hosei.media.lectcast.web.serializer.LectcastSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 100 * 60)
public class HttpSessionConfig extends AbstractHttpSessionApplicationInitializer {

  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private int redisPort;

  @Bean
  public JedisConnectionFactory connectionFactory() {
    final RedisStandaloneConfiguration hostConfiguration = new RedisStandaloneConfiguration();
    hostConfiguration.setHostName(redisHost);
    hostConfiguration.setPort(redisPort);

    final JedisClientConfiguration.JedisClientConfigurationBuilder clientConfigurationBuilder = JedisClientConfiguration
        .builder();
    clientConfigurationBuilder.usePooling();
    clientConfigurationBuilder.connectTimeout(Duration.ofSeconds(60));

    return new JedisConnectionFactory(hostConfiguration, clientConfigurationBuilder.build());
  }

  @Bean
  public JedisPoolConfig jedisPoolConfig() {
    final JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(10);
    poolConfig.setMaxIdle(5);
    poolConfig.setMinIdle(1);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    poolConfig.setTestWhileIdle(true);
    return poolConfig;
  }

  @Bean
  @Qualifier("springSessionDefaultRedisSerializer")
  public RedisSerializer<Object> redisSerializer() {
    return new LectcastSerializer();
  }
}
