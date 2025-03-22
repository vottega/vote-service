package vottega.vote_service.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
  @Bean
  fun redisTemplate(
    connectionFactory: RedisConnectionFactory,
    redisSerializer: RedisSerializer<Any>
  ): RedisTemplate<String, Any> {
    val template = RedisTemplate<String, Any>()
    template.connectionFactory = connectionFactory
    template.keySerializer = StringRedisSerializer()
    template.valueSerializer = redisSerializer
    template.hashKeySerializer = StringRedisSerializer()
    template.hashValueSerializer = redisSerializer
    return template
  }

  @Bean
  fun redisSerializer(): RedisSerializer<Any> {
    val objectMapper = ObjectMapper()
      .registerModule(JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    return GenericJackson2JsonRedisSerializer(objectMapper)
  }
}