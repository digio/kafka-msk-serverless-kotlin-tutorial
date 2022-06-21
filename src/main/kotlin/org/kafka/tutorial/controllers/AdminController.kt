package org.kafka.tutorial.controllers

import org.kafka.tutorial.admin.TopicsManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController @Autowired constructor(
    private val topicsManager: TopicsManager, private val kafkaListenerEndpointRegistry: KafkaListenerEndpointRegistry
) {

    @PostMapping("/topic")
    fun createTopic(@RequestBody topicName: String) {
        return topicsManager.createTopic(topicName)
    }

    @GetMapping("/topic/all")
    fun topics(): Set<String> {
        return topicsManager.listTopics()
    }

    @PostMapping("/consumers/start")
    fun startConsumers() {
        kafkaListenerEndpointRegistry.start();
    }
}
