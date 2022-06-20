package org.kafka.tutorial.config

import com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryKafkaDeserializer
import com.amazonaws.services.schemaregistry.serializers.GlueSchemaRegistryKafkaSerializer
import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants
import com.amazonaws.services.schemaregistry.utils.AvroRecordType
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import software.amazon.awssdk.services.glue.model.Compatibility

@Configuration
class KafkaConfig {
    @Value("\${bootstrap.url}")
    private val bootstrapUrl: String? = null

    @Value("\${aws.region}")
    private val awsRegion: String? = null

    @Value("\${message.group.id}")
    private val groupId: String? = null

    @Value("\${schema.name}")
    private val schemaName: String? = null

    @Value("\${schema.registry.name}")
    private val schemaRegistryName: String? = null

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    fun connectionProperties(): MutableMap<String, Any?> {
        val properties: MutableMap<String, Any?> = HashMap()

        // Bootstrap server endpoint for the cluster
        properties[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapUrl

        // AWS Security settings
        properties[AdminClientConfig.SECURITY_PROTOCOL_CONFIG] = "SASL_SSL"
        properties[SaslConfigs.SASL_MECHANISM] = "AWS_MSK_IAM"
        properties[SaslConfigs.SASL_JAAS_CONFIG] = "software.amazon.msk.auth.iam.IAMLoginModule required"
        properties[SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS] = "software.amazon.msk.auth.iam.IAMClientCallbackHandler"

        return properties
    }

    @Bean
    fun consumerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    private fun consumerFactory(): ConsumerFactory<String, String> {
        val properties: MutableMap<String, Any?> = HashMap(connectionProperties())

        properties[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        properties[ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG] = "20971520"
        properties[ConsumerConfig.FETCH_MAX_BYTES_CONFIG] = "20971520"
        properties[ConsumerConfig.RETRY_BACKOFF_MS_CONFIG] = "1000"

        // Schema Registry Settings
        properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = GlueSchemaRegistryKafkaDeserializer::class.java.name
        properties[AWSSchemaRegistryConstants.AWS_REGION] = "us-east-2" // Pass an AWS Region
        properties[AWSSchemaRegistryConstants.AVRO_RECORD_TYPE] = AvroRecordType.GENERIC_RECORD.getName() // Only required for AVRO data format

        return DefaultKafkaConsumerFactory(properties)
    }

    private fun producerFactory(): ProducerFactory<String, String> {
        val properties: MutableMap<String, Any?> = HashMap(connectionProperties())

        properties[ProducerConfig.RETRIES_CONFIG] = 3
        properties[ProducerConfig.RETRY_BACKOFF_MS_CONFIG] = 1000

        // Schema Registry Settings
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = GlueSchemaRegistryKafkaSerializer::class.java.name

        properties[AWSSchemaRegistryConstants.AWS_REGION] = awsRegion
        properties[AWSSchemaRegistryConstants.DATA_FORMAT] = "JSON" // OR "AVRO"

        properties[AWSSchemaRegistryConstants.SCHEMA_AUTO_REGISTRATION_SETTING] = "true" // If not passed, uses "false"
        properties[AWSSchemaRegistryConstants.SCHEMA_NAME] =
            schemaName// If not passed, uses transport name (topic name in case of Kafka, or stream name in case of Kinesis Data Streams)
        properties[AWSSchemaRegistryConstants.REGISTRY_NAME] = schemaRegistryName // If not passed, uses "default-registry"
        properties[AWSSchemaRegistryConstants.CACHE_TIME_TO_LIVE_MILLIS] = "86400000" // If not passed, uses 86400000 (24 Hours)
        properties[AWSSchemaRegistryConstants.CACHE_SIZE] = "10" // default value is 200
        properties[AWSSchemaRegistryConstants.COMPATIBILITY_SETTING] =
            Compatibility.FULL // Pass a compatibility mode. If not passed, uses Compatibility.BACKWARD
        properties[AWSSchemaRegistryConstants.COMPRESSION_TYPE] = AWSSchemaRegistryConstants.COMPRESSION.ZLIB // If not passed, records are sent uncompressed
        return DefaultKafkaProducerFactory(properties)
    }
}
