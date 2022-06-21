package org.kafka.tutorial.producers

import mu.KotlinLogging
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.kafka.tutorial.KafkaMskServerlessApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import org.springframework.util.concurrent.ListenableFutureCallback
import java.io.File

private val log = KotlinLogging.logger {}

@Component
class SimpleMessageProducer
@Autowired constructor(private val kafkaTemplate: KafkaTemplate<String, GenericData.Record>) {

    @Value(value = "\${message.topic.name}")
    private val topicName: String? = null
    fun sendMessage(message: String) {
        val bookClubSchema = Schema.Parser().parse(File(KafkaMskServerlessApplication::class.java.getResource("/BookClub.avsc").toURI()))
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
