package org.kafka.tutorial.controllers

import org.kafka.tutorial.domain.BookClub
import org.kafka.tutorial.producers.BookClubAvroProducer
import org.kafka.tutorial.producers.SimpleMessageProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/message")
class ProducerController @Autowired constructor(
    private val simpleMessageProducer: SimpleMessageProducer,
    private val bookClubAvroProducer: BookClubAvroProducer
) {

    @PostMapping("/simple")
    fun publish(@RequestBody message: String) {
        simpleMessageProducer.produce(message)
    }

    @PostMapping("/bookclub")
    fun publish(@RequestBody bookClub: BookClub) {
        bookClubAvroProducer.produce(bookClub)
    }
}
