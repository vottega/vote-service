package vottega.vote_service.config.kafka

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import vottega.avro.ParticipantAvro

@EnableKafka
@Configuration
class ParticipantConsumerConfig(private val kafkaCommonConfig: KafkaCommonConfig) {
  @Bean
  fun roomConsumerFactor(): ConsumerFactory<Long, ParticipantAvro> {
    return DefaultKafkaConsumerFactory(kafkaCommonConfig.commonConsumerConfig())
  }

  @Bean
  fun participantKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<Long, ParticipantAvro> {
    val factory = ConcurrentKafkaListenerContainerFactory<Long, ParticipantAvro>()
    factory.consumerFactory = roomConsumerFactor()
    return factory
  }
}