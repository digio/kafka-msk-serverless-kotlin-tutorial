package org.kafka.tutorial.config

import com.squareup.wire.internal.toUnmodifiableMap
import org.apache.kafka.clients.admin.AdminClientConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("development")
class LocalKafkaConnectionConfig {
    @Value("\${bootstrap.url}")
    private val bootstrapUrl: String? = null

    @Bean
    fun connectionProperties(): Map<String, Any?> {
        val properties: MutableMap<String, Any?> = HashMap()

        // Bootstrap server endpoint for the cluster
        properties[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapUrl

        return properties.toUnmodifiableMap()
    }
}
