package org.kafka.tutorial.config

import com.squareup.wire.internal.toUnmodifiableMap
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.common.config.SaslConfigs
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.msk.auth.iam.IAMClientCallbackHandler
import software.amazon.msk.auth.iam.IAMLoginModule

@Configuration
class ClusterConfig {
    @Value("\${bootstrap.url}")
    private val bootstrapUrl: String? = null

    @Bean
    fun mskServerlessConnectionProperties(): Map<String, Any?> {
        val properties: MutableMap<String, Any?> = HashMap()

        // Bootstrap server endpoint for the cluster
        properties[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapUrl

        // AWS Security settings for MSK serverless connection
        properties[AdminClientConfig.SECURITY_PROTOCOL_CONFIG] = "SASL_SSL"

        properties[SaslConfigs.SASL_MECHANISM] = IAMLoginModule.MECHANISM
        properties[SaslConfigs.SASL_JAAS_CONFIG] = "${IAMLoginModule::class.java.name} required;"
        properties[SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS] = IAMClientCallbackHandler::class.java.name

        return properties.toUnmodifiableMap()
    }
}
