package org.kafka.tutorial.consumers

import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component


private val log = KotlinLogging.logger {}

@Component
class SimpleMessageConsumer {

    @KafkaListener(
        topics = ["\${topic.simple.name}"],
        groupId = "\${topic.simple.group}",
        autoStartup = "false",
        containerFactory = "simpleConsumerContainerFactory",
    )
    fun listen(message: String) {
        log.info("Received a simple message: $message")
    }
}
