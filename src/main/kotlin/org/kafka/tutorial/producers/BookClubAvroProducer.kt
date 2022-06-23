package org.kafka.tutorial.producers

import mu.KotlinLogging
import org.kafka.tutorial.domain.BookClub
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import org.springframework.util.concurrent.ListenableFutureCallback

private val log = KotlinLogging.logger {}

@Component
class BookClubAvroProducer
@Autowired constructor(private val avroKafkaTemplate: KafkaTemplate<String, BookClub>) {

    @Value(value = "\${topic.bookclub.name}")
    private val topicName: String? = null

    fun produce(bookClub: BookClub) {
        val future = avroKafkaTemplate.send(topicName!!, bookClub)
        future.addCallback(object : ListenableFutureCallback<SendResult<String?, BookClub>?> {
            override fun onSuccess(result: SendResult<String?, BookClub>?) {
                log.info("Sent message=[$bookClub] with offset=[${result!!.recordMetadata.offset()}]")
            }

            override fun onFailure(ex: Throwable) {
                log.info("Unable to send message=[$bookClub] due to : ${ex.message}")
            }
        })
    }
}
