package org.kafka.tutorial

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class KafkaMskServerlessApplication

fun main(args: Array<String>) {
    SpringApplication.run(KafkaMskServerlessApplication::class.java, *args)
}
