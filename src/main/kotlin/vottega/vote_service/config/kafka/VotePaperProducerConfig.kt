package vottega.vote_service.config.kafka

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import vottega.vote_service.avro.VotePaperAvro

@Configuration
class VotePaperProducerConfig(private val kafkaCommonConfig: KafkaCommonConfig) {
  @Bean
  fun votePaperProducerFactory(): ProducerFactory<Long, VotePaperAvro> {
    return DefaultKafkaProducerFactory(kafkaCommonConfig.commonProducerConfig())
  }

  @Bean
  fun votePaperKafkaTemplate(): KafkaTemplate<Long, VotePaperAvro> {
    return KafkaTemplate(votePaperProducerFactory())
  }

  @Bean
  fun makeVotePaperTopic(): NewTopic {
    return NewTopic("vote-paper", 1, 1)
  }
}