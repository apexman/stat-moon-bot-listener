package ru.apexman.statmoonbotlistener.controller

import org.apache.logging.log4j.LogManager
import org.springframework.messaging.Message
import org.springframework.messaging.MessageDeliveryException
import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler


@ControllerAdvice
class StompErrorHandler : StompSubProtocolErrorHandler() {
    private val logger = LogManager.getLogger(StompErrorHandler::class.java)


    @MessageExceptionHandler
    fun handleException(e: MessageDeliveryException): WebSocketMessage<*> {
        logger.error("Got handleClientMessageProcessingError", e)
        return TextMessage(e.javaClass.name)
    }

    override fun handleClientMessageProcessingError(
        clientMessage: Message<ByteArray>?,
        ex: Throwable,
    ): Message<ByteArray>? {
        logger.error("Got handleClientMessageProcessingError", ex)
        return super.handleClientMessageProcessingError(clientMessage, ex)
    }

    override fun handleErrorMessageToClient(errorMessage: Message<ByteArray>): Message<ByteArray>? {
        logger.error("Got handleErrorMessageToClient: '$errorMessage'")
        return super.handleErrorMessageToClient(errorMessage)
    }

    override fun handleInternal(
        errorHeaderAccessor: StompHeaderAccessor,
        errorPayload: ByteArray,
        cause: Throwable?,
        clientHeaderAccessor: StompHeaderAccessor?,
    ): Message<ByteArray> {
        logger.error("Got handleInternal: '$errorPayload'", cause)
        return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor)
    }
}