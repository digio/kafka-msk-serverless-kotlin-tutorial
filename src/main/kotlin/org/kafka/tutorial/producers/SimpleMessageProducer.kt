package org.kafka.tutorial.producers

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import org.springframework.util.concurrent.ListenableFutureCallback

private val log = KotlinLogging.logger {}

@Component
class SimpleMessageProducer
    @Autowired constructor(private val kafkaTemplate: KafkaTemplate<String, String>) {

    @Value(value = "\${message.topic.name}")
    private val topicName: String? = null
    fun sendMessage(message: String) {
        val future = kafkaTemplate.send(topicName!!, message)
        future.addCallback(object : ListenableFutureCallback<SendResult<String?, String?>?> {
            override fun onSuccess(result: SendResult<String?, String?>?) {
                log.info("Sent message=[$message] with offset=[${result!!.recordMetadata.offset()}]")
            }

            override fun onFailure(ex: Throwable) {
                log.info("Unable to send message=[$message] due to : ${ex.message}")
            }
        })
    }
}
