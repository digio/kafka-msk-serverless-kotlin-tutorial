package org.kafka.tutorial.consumers

import mu.KotlinLogging
import org.kafka.tutorial.domain.BookClub
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component


private val log = KotlinLogging.logger {}

@Component
class BookClubAvroMessageConsumer {

    @KafkaListener(
        topics = ["\${topic.bookclub.name}"],
        groupId = "\${topic.bookclub.group}",
        autoStartup = "false",
        containerFactory = "avroConsumerContainerFactory",
    )
    fun listen(bookClub: BookClub) {
        log.info("Received a book club message: $bookClub")
    }
}
