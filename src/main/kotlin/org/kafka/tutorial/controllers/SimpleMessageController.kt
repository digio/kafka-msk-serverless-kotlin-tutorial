package org.kafka.tutorial.controllers

import org.kafka.tutorial.producers.SimpleMessageProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/simple")
class SimpleMessageController @Autowired constructor(private val producer: SimpleMessageProducer) {

    @PostMapping("/message")
    fun publish(@RequestBody message: String) {
        producer.sendMessage(message)
    }
}
