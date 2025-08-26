package vottega.vote_service.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import vottega.vote_service.dto.room.ParticipantResponseDTO

@Configuration
class RedisConfig(
  private val connectionFactory: RedisConnectionFactory
) {
  @Bean
  fun participantRedisTemplate(
    objectMapper: ObjectMapper
  ): RedisTemplate<String, ParticipantResponseDTO> {
    val template = RedisTemplate<String, ParticipantResponseDTO>()
    template.connectionFactory = connectionFactory
    template.keySerializer = StringRedisSerializer()
    template.hashKeySerializer = StringRedisSerializer()
    val serializer = GenericJackson2JsonRedisSerializer(objectMapper)
    template.valueSerializer = serializer
    template.hashValueSerializer = serializer
    template.afterPropertiesSet()

    return template
  }

  @Bean
  fun longRedisTemplate(): RedisTemplate<String, Long> {

    val template = RedisTemplate<String, Long>()
    template.connectionFactory = connectionFactory


    template.keySerializer = StringRedisSerializer()
    template.valueSerializer = Jackson2JsonRedisSerializer(Long::class.java)

    template.hashKeySerializer = StringRedisSerializer()
    template.hashValueSerializer = Jackson2JsonRedisSerializer(Long::class.java)

    return template
  }


  @Bean
  fun stringRedisTemplate() = StringRedisTemplate(connectionFactory)

}