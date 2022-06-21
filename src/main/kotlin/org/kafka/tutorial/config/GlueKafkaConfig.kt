package org.kafka.tutorial.config

import com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryKafkaDeserializer
import com.amazonaws.services.schemaregistry.serializers.GlueSchemaRegistryKafkaSerializer
import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants
import com.amazonaws.services.schemaregistry.utils.AvroRecordType
import org.apache.avro.generic.GenericData
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
import software.amazon.awssdk.services.glue.model.DataFormat

@Configuration
class GlueKafkaConfig {

    @Value("\${aws.region}")
    private val awsRegion: String? = null

    @Value("\${topic.bookclub.group}")
    private val groupId: String? = null

    @Value("\${topic.bookclub.schema}")
    private val schemaName: String? = null

    @Value("\${topic.bookclub.registry}")
    private val schemaRegistryName: String? = null

    @Bean
    fun avroKafkaTemplate(@Autowired mskServerlessConnectionProperties: Map<String, Any?>): KafkaTemplate<String, GenericData.Record> {
        val properties: MutableMap<String, Any?> = HashMap(mskServerlessConnectionProperties)

        properties[ProducerConfig.RETRIES_CONFIG] = 3
        properties[ProducerConfig.RETRY_BACKOFF_MS_CONFIG] = 1000

        // AWS Schema Registry Settings
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = GlueSchemaRegistryKafkaSerializer::class.java.name

        properties[AWSSchemaRegistryConstants.AWS_REGION] = awsRegion
        properties[AWSSchemaRegistryConstants.DATA_FORMAT] = DataFormat.AVRO.name

        properties[AWSSchemaRegistryConstants.SCHEMA_NAME] =
            schemaName// If not passed, uses transport name (topic name in case of Kafka, or stream name in case of Kinesis Data Streams)
        properties[AWSSchemaRegistryConstants.REGISTRY_NAME] = schemaRegistryName // If not passed, uses "default-registry"

        properties[AWSSchemaRegistryConstants.SCHEMA_AUTO_REGISTRATION_SETTING] = "true" // If not passed, uses "false"
        properties[AWSSchemaRegistryConstants.CACHE_TIME_TO_LIVE_MILLIS] = "86400000" // If not passed, uses 86400000 (24 Hours)
        properties[AWSSchemaRegistryConstants.CACHE_SIZE] = "10" // default value is 200
        val producerFactory = DefaultKafkaProducerFactory<String, GenericData.Record>(properties)

        return KafkaTemplate(producerFactory)
    }

    @Bean
    fun avroConsumerContainerFactory(@Autowired mskServerlessConnectionProperties: Map<String, Any?>): ConcurrentKafkaListenerContainerFactory<String, GenericData.Record> {
        val properties: MutableMap<String, Any?> = HashMap(mskServerlessConnectionProperties)

        properties[ConsumerConfig.GROUP_ID_CONFIG] = groupId

        // Schema Registry Settings
        properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = GlueSchemaRegistryKafkaDeserializer::class.java.name

        properties[AWSSchemaRegistryConstants.AWS_REGION] = awsRegion
        properties[AWSSchemaRegistryConstants.AVRO_RECORD_TYPE] = AvroRecordType.GENERIC_RECORD.getName() // Only required for AVRO data format

        val factory = ConcurrentKafkaListenerContainerFactory<String, GenericData.Record>()
        factory.consumerFactory = DefaultKafkaConsumerFactory(properties)
        return factory
    }
}
