package org.kafka.tutorial.producers

import com.amazonaws.services.schemaregistry.serializers.GlueSchemaRegistryKafkaSerializer
import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants
import mu.KotlinLogging
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import org.springframework.util.concurrent.ListenableFutureCallback
import software.amazon.awssdk.services.glue.model.DataFormat

private val log = KotlinLogging.logger {}

@Component
class SimpleMessageProducer
@Autowired constructor(private val kafkaTemplate: KafkaTemplate<String, GenericData.Record>) {

    val bookClubSchemaString = """
        {
          "type": "record",
          "namespace": "BookClub",
          "name": "Book",
          "fields": [
            {
              "name": "Name",
              "type": "string"
            },
            {
              "name": "Author",
              "type": "string"
            },
            {
              "name": "Genre",
              "type": "string"
            },
            {
              "name": "Rating",
              "type": "int"
            }
          ]
        }
    """.trimIndent();

    @Value(value = "\${message.topic.name}")
    private val topicName: String? = null
    fun sendMessage(message: String) {
//        val bookClubSchema = Schema.Parser().parse(File(SimpleMessageProducer::class.java.getResource("/BookClub.avsc").toURI()))
        val bookClubSchema = Schema.Parser().parse(bookClubSchemaString)
        val bookClub = GenericData.Record(bookClubSchema)
        bookClub.put("Name", "test");
        bookClub.put("Author", "test");
        bookClub.put("Genre", "test");
        bookClub.put("Rating", 1);
        println(bookClub)

        val properties: MutableMap<String, Any?> = HashMap()

        properties[ProducerConfig.RETRIES_CONFIG] = 3
        properties[ProducerConfig.RETRY_BACKOFF_MS_CONFIG] = 1000

        // AWS Schema Registry Settings
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = GlueSchemaRegistryKafkaSerializer::class.java.name

        properties[AWSSchemaRegistryConstants.AWS_REGION] = "us-east-1"
        properties[AWSSchemaRegistryConstants.DATA_FORMAT] = DataFormat.AVRO.name

        properties[AWSSchemaRegistryConstants.SCHEMA_NAME] =
            "msk-serverless-tutorial-schema"// If not passed, uses transport name (topic name in case of Kafka, or stream name in case of Kinesis Data Streams)
        properties[AWSSchemaRegistryConstants.REGISTRY_NAME] = "msk-serverless-tutorial-registry" // If not passed, uses "default-registry"

        properties[AWSSchemaRegistryConstants.SCHEMA_AUTO_REGISTRATION_SETTING] = "true" // If not passed, uses "false"
        properties[AWSSchemaRegistryConstants.CACHE_TIME_TO_LIVE_MILLIS] = "86400000" // If not passed, uses 86400000 (24 Hours)
        properties[AWSSchemaRegistryConstants.CACHE_SIZE] = "10"
        println(String(GlueSchemaRegistryKafkaSerializer(properties).serialize("ewwewew", bookClub)));
        val future = kafkaTemplate.send(topicName!!, bookClub)
        future.addCallback(object : ListenableFutureCallback<SendResult<String?, GenericData.Record?>?> {
            override fun onSuccess(result: SendResult<String?, GenericData.Record?>?) {
                log.info("Sent message=[$message] with offset=[${result!!.recordMetadata.offset()}]")
            }

            override fun onFailure(ex: Throwable) {
                log.info("Unable to send message=[$message] due to : ${ex.message}")
            }
        })
    }
}
