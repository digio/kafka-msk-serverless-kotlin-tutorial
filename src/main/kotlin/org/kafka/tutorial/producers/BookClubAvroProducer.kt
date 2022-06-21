package org.kafka.tutorial.producers

import mu.KotlinLogging
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
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
@Autowired constructor(private val avroKafkaTemplate: KafkaTemplate<String, GenericData.Record>) {

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

    @Value(value = "\${topic.bookclub.name}")
    private val topicName: String? = null

    fun produce(bookClub: BookClub) {
        val bookClubSchema = Schema.Parser().parse(bookClubSchemaString)
        val bookClubRecord = GenericData.Record(bookClubSchema)
        bookClubRecord.put("Name", bookClub.name);
        bookClubRecord.put("Author", bookClub.author);
        bookClubRecord.put("Genre", bookClub.genre);
        bookClubRecord.put("Rating", bookClub.rating);

        val future = avroKafkaTemplate.send(topicName!!, bookClubRecord)
        future.addCallback(object : ListenableFutureCallback<SendResult<String?, GenericData.Record?>?> {
            override fun onSuccess(result: SendResult<String?, GenericData.Record?>?) {
                log.info("Sent message=[$bookClub] with offset=[${result!!.recordMetadata.offset()}]")
            }

            override fun onFailure(ex: Throwable) {
                log.info("Unable to send message=[$bookClub] due to : ${ex.message}")
            }
        })
    }
}
