package vottega.vote_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import vottega.vote_service.domain.Vote;

@EnableKafka
@Configuration
class VoteKafkaConfig {

    @Bean
    fun participantConsumerFactory():ConsumerFactory<String, Vote>
    {
        val config = mutableMapOf<String, Any>(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
                ConsumerConfig.GROUP_ID_CONFIG to "participant-group",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to "io.confluent.kafka.serializers.KafkaAvroDeserializer",
                "schema.registry.url" to "http://localhost:8081",
                "specific.avro.reader" to true
        )
        return DefaultKafkaConsumerFactory(config)
    }

    @Bean
    fun participantKafkaListenerContainerFactory():ConcurrentKafkaListenerContainerFactory<String, Vote>
    {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Participant>()
        factory.consumerFactory = participantConsumerFactory()
        return factory
    }
}