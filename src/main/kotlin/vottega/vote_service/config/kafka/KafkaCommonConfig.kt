package vottega.vote_service.config.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.common.serialization.LongSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class KafkaCommonConfig(
  @Value("\${spring.kafka.bootstrap-servers}") private val bootstrapServers: String,
  @Value("\${spring.kafka.consumer.group-id}") private val defaultGroupId: String,
  @Value("\${spring.kafka.properties.schema.registry.url}") private val schemaRegistryUrl: String
) {
  fun commonConsumerConfig(): Map<String, Any> {
    return mapOf(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
      ConsumerConfig.GROUP_ID_CONFIG to defaultGroupId,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to LongDeserializer::class.java.name,
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to "io.confluent.kafka.serializers.KafkaAvroDeserializer",
      "schema.registry.url" to schemaRegistryUrl,
      "specific.avro.reader" to true
    )
  }

  fun commonProducerConfig(): Map<String, Any> {
    return mapOf(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to LongSerializer::class.java.name,
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to "io.confluent.kafka.serializers.KafkaAvroSerializer",
      "schema.registry.url" to schemaRegistryUrl
    )
  }
}