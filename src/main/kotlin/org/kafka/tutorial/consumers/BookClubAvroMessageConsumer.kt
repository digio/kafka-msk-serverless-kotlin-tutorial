package org.kafka.tutorial.consumers

import mu.KotlinLogging
import org.apache.avro.generic.GenericData
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
    fun listen(bookClub: GenericData.Record) {
        log.info("Received a book club message: $bookClub")
    }
}
