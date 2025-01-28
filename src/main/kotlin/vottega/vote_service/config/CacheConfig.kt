package vottega.vote_service.config

import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import java.time.Duration


@Configuration
class CacheConfig {
  @Bean
  fun cacheManager(redisConnectionFactory: RedisConnectionFactory): CacheManager {
    val config: RedisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
      .entryTtl(Duration.ofMinutes(60))
      .disableCachingNullValues()

    return RedisCacheManager.builder(redisConnectionFactory)
      .cacheDefaults(config)
      .build()
  }
}