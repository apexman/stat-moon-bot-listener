package ru.apexman.statmoonbotlistener.service

import jakarta.annotation.PostConstruct
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.sockjs.client.SockJsClient
import ru.apexman.statmoonbotlistener.config.AppConfigProperties
import ru.apexman.statmoonbotlistener.service.notification.TelegramNotificationService
import java.net.URI


@Service
class OutsideSocketJsClient(
    val sockJsClient: SockJsClient,
    val appConfigProperties: AppConfigProperties,
    val localSocketJsClient: LocalSocketJsClient,
    val telegramNotificationService: TelegramNotificationService,
    private val logger: Logger = LogManager.getLogger(OutsideSocketJsClient::class.java),
) : AbstractFockingClient(logger) {
    var clientSession: WebSocketSession? = null

    @PostConstruct
    fun postConstruct() {
        connect()
    }

    @Scheduled(fixedDelay = 5_000)
    fun reconnect() {
        if (!isConnected()) {
            logger.warn("Try to connect to outside server")
            connect()
        }
    }

    fun connect() {
        try {
            this.clientSession =
                sockJsClient
                    .execute(
                        this,
                        WebSocketHttpHeaders(),
                        URI.create(appConfigProperties.moonTradesUrl)
                    )
                    .get()
        } catch (e: Exception) {
            logger.error(e)
            clientSession = null
            telegramNotificationService.sendMonitoring(e.toString(), TelegramNotificationService.buildTelegramDocumentDto(e))
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        super.afterConnectionEstablished(session)
        telegramNotificationService.sendMonitoring("Outside sockJs client connected")
        val text =
            "CONNECT\naccept-version:1.1,1.0\nheart-beat:10000,10000\n\n\u0000"
        val connectMessage = TextMessage(text)
        session.sendMessage(connectMessage)
        logger.debug("Sent message: '$connectMessage'")
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        telegramNotificationService.sendMonitoring("Outside sockJs client connection closed with status $status")
        super.afterConnectionClosed(session, status)
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        telegramNotificationService.sendMonitoring(exception.toString(),
            TelegramNotificationService.buildTelegramDocumentDto(Exception(exception)))
        super.handleTransportError(session, exception)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        super.handleTextMessage(session, message)
        if (message.payload.contains("CONNECTED")) {
            val replies = listOf(
                TextMessage("SUBSCRIBE\nid:sub-0\ndestination:/data/trades\n\n\u0000"),
                TextMessage("SUBSCRIBE\nid:sub-1\ndestination:/data/sum\n\n\u0000")
            )
            for (reply in replies) {
                clientSession?.sendMessage(reply)
                logger.debug("Replied with '$reply'")
            }
        } else {
            localSocketJsClient.sendTextMessage(message)
        }
    }

    fun isConnected(): Boolean {
        return clientSession?.isOpen ?: false
    }
}