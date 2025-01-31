package vottega.vote_service.config.kafka

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import vottega.vote_service.avro.VoteAvro

@Configuration
class VoteProducerConfig(private val kafkaCommonConfig: KafkaCommonConfig) {
  @Bean
  fun voteProducerFactory(): ProducerFactory<Long, VoteAvro> {
    return DefaultKafkaProducerFactory(kafkaCommonConfig.commonProducerConfig())
  }

  @Bean
  fun voteKafkaTemplate(): KafkaTemplate<Long, VoteAvro> {
    return KafkaTemplate(voteProducerFactory())
  }

  @Bean
  fun makeVoteTopic(): NewTopic {
    return NewTopic("vote", 1, 1)
  }
}