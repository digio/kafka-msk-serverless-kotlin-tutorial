package org.kafka.tutorial.admin

import mu.KotlinLogging
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.PartitionInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.function.Consumer

private val log = KotlinLogging.logger {}

@Component
class TopicsManager @Autowired constructor(private val connectionProperties: Map<String, Any?>) {
    fun createTopic(topicName: String?) {
        val topic = NewTopic(topicName, Optional.of(1), Optional.empty())
        val topics = AdminClient.create(connectionProperties).createTopics(java.util.List.of(topic))
        val values = topics.values()
        log.info("Number of topics created ${values.size}")
        log.info(values.toString())
        try {
            topics.all().get()
        } catch (e: InterruptedException) {
            throw RuntimeException("Error while creating the topic", e)
        } catch (e: ExecutionException) {
            throw RuntimeException("Error while creating the topic", e)
        }
        log.info("Topic $topicName is successfully created")
    }

    fun listTopics(): Set<String> {
        val consumer = KafkaConsumer<String, String>(connectionProperties)
        val topics = consumer.listTopics()
        log.info("Number of existing topics ${topics.size}")
        topics.forEach { (s: String?, partitionInfos: List<PartitionInfo>) ->
            log.info("Topic Name $s")
            partitionInfos.forEach(Consumer { x: PartitionInfo ->
                log.info(x.toString())
            })
        }
        return topics.keys
    }
}
