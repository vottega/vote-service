package vottega.vote_service.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import vottega.vote_service.dto.room.ParticipantResponseDTO

@Configuration
class RedisConfig {
  @Bean
  fun participantRedisTemplate(
    connectionFactory: RedisConnectionFactory
  ): RedisTemplate<String, ParticipantResponseDTO> {
    val template = RedisTemplate<String, ParticipantResponseDTO>()
    template.connectionFactory = connectionFactory

    val objectMapper = ObjectMapper()
      .registerKotlinModule()
      .registerModule(JavaTimeModule()) // LocalDateTime 등 직렬화/역직렬화 지원
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    val serializer = Jackson2JsonRedisSerializer(objectMapper, ParticipantResponseDTO::class.java)

    // (4) RedisTemplate Serializer에 적용
    template.keySerializer = StringRedisSerializer()
    template.valueSerializer = serializer
    template.hashKeySerializer = StringRedisSerializer()
    template.hashValueSerializer = serializer

    return template
  }

  @Bean
  fun longRedisTemplate(
    connectionFactory: RedisConnectionFactory
  ): RedisTemplate<String, Long> {

    val template = RedisTemplate<String, Long>()
    template.connectionFactory = connectionFactory


    template.keySerializer = StringRedisSerializer()
    template.valueSerializer = Jackson2JsonRedisSerializer(Long::class.java)

    template.hashKeySerializer = StringRedisSerializer()
    template.hashValueSerializer = Jackson2JsonRedisSerializer(Long::class.java)

    return template
  }

}