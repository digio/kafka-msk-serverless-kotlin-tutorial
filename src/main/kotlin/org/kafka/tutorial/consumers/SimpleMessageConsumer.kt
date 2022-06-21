package org.kafka.tutorial.consumers

import mu.KotlinLogging
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class SimpleMessageConsumer {

//    @KafkaListener(
//        topics = ["\${message.topic.name}"],
//        groupId = "\${message.group.id}",
//        containerFactory = "consumerContainerFactory",
//    )
    fun listen(message: String) {
        log.info("Received a simple message: $message")
    }
}
