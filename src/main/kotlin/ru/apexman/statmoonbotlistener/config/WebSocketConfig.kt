package ru.apexman.statmoonbotlistener.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import ru.apexman.statmoonbotlistener.controller.StompErrorHandler


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    val stompErrorHandler: StompErrorHandler,
) : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.setApplicationDestinationPrefixes("/data")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.setErrorHandler(stompErrorHandler)
        registry
            .addEndpoint("/local-websocket")
            .setAllowedOriginPatterns("*")
            .setHandshakeHandler(DefaultHandshakeHandler(TomcatRequestUpgradeStrategy()))
            .withSockJS()
    }
}