package ru.apexman.statmoonbotlistener.service

import org.apache.logging.log4j.Logger
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.PongMessage
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler


abstract class AbstractFockingClient(
    private val logger: Logger
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.trace("Got afterConnectionEstablished: ${session.id}")
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        logger.warn("Connection closed '$session' with status '$status'")
        super.afterConnectionClosed(session, status)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        logger.trace("Got handleTextMessage: '$message'")
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.error("Got handleTransportError in session '$session'", exception)
    }

    override fun handlePongMessage(session: WebSocketSession, message: PongMessage) {
        logger.trace("Got handlePongMessage: '$message'")
        super.handlePongMessage(session, message)
    }
}