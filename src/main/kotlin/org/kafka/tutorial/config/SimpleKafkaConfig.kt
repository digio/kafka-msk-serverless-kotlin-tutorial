package org.kafka.tutorial.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate

@Configuration
class SimpleKafkaConfig {
    @Value("\${topic.simple.group}")
    private val groupId: String? = null

    @Bean
    fun simpleKafkaTemplate(@Autowired connectionProperties: Map<String, Any?>): KafkaTemplate<String, String> {
        val properties: MutableMap<String, Any?> = HashMap(connectionProperties)

        properties[ProducerConfig.RETRIES_CONFIG] = 3
        properties[ProducerConfig.RETRY_BACKOFF_MS_CONFIG] = 1000

        // AWS Schema Registry Settings
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name

        return KafkaTemplate(DefaultKafkaProducerFactory(properties))
    }

    @Bean
    fun simpleConsumerContainerFactory(@Autowired connectionProperties: Map<String, Any?>): ConcurrentKafkaListenerContainerFactory<String, String> {
        val properties: MutableMap<String, Any?> = HashMap(connectionProperties)

        properties[ConsumerConfig.GROUP_ID_CONFIG] = groupId

        // Schema Registry Settings
        properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name

        val consumerFactory = DefaultKafkaConsumerFactory<String, String>(properties)
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory

        return factory
    }
}
