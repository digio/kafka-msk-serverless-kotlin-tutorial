package org.kafka.tutorial.controllers

import org.kafka.tutorial.admin.TopicsManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController @Autowired constructor(private val topicsManager: TopicsManager) {

    @GetMapping("/topics")
    fun topics(): Set<String> {
        return topicsManager.listTopics()
    }
}
