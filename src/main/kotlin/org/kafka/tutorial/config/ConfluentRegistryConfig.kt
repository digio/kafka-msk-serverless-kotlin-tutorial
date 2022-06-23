package org.kafka.tutorial.config

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.kafka.tutorial.domain.BookClub
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate

@Configuration
@ConditionalOnProperty(
    value=["schema.registry.name"],
    havingValue = "confluent",
    matchIfMissing = false)
class ConfluentRegistryConfig {

    @Value("\${schema.registry.url}")
    private val schemaRegistryUrl: String? = null

    @Value("\${topic.bookclub.group}")
    private val groupId: String? = null

    @Bean
    fun avroKafkaTemplate(@Autowired connectionProperties: Map<String, Any?>): KafkaTemplate<String, BookClub> {
        val properties: MutableMap<String, Any?> = HashMap(connectionProperties)

        properties[ProducerConfig.RETRIES_CONFIG] = 3
        properties[ProducerConfig.RETRY_BACKOFF_MS_CONFIG] = 1000

        // AWS Schema Registry Settings
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java.name
        properties[AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG] = schemaRegistryUrl
        properties["specific.avro.reader"] = "true"

        val producerFactory = DefaultKafkaProducerFactory<String, BookClub>(properties)

        return KafkaTemplate(producerFactory)
    }

    @Bean
    fun avroConsumerContainerFactory(@Autowired mskServerlessConnectionProperties: Map<String, Any?>): ConcurrentKafkaListenerContainerFactory<String, BookClub> {
        val properties: MutableMap<String, Any?> = HashMap(mskServerlessConnectionProperties)

        properties[ConsumerConfig.GROUP_ID_CONFIG] = groupId

        // Schema Registry Settings
        properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = KafkaAvroDeserializer::class.java.name
        properties[KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG] = true;
        properties[AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG] = schemaRegistryUrl;

        val factory = ConcurrentKafkaListenerContainerFactory<String, BookClub>()
        factory.consumerFactory = DefaultKafkaConsumerFactory(properties)
        return factory
    }
}
