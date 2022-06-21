package org.kafka.tutorial.producers

import mu.KotlinLogging
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import org.springframework.util.concurrent.ListenableFutureCallback

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
        bookClub.put("Rating", "test");
        println(bookClub)

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
