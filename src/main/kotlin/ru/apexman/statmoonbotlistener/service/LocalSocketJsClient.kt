package ru.apexman.statmoonbotlistener.service

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent
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
class LocalSocketJsClient(
    val sockJsClient: SockJsClient,
    val appConfigProperties: AppConfigProperties,
    val telegramNotificationService: TelegramNotificationService,
    private val logger: Logger = LogManager.getLogger(LocalSocketJsClient::class.java),
) : AbstractFockingClient(logger) {
    var clientSession: WebSocketSession? = null
    var isBrokerAvailable: Boolean = false

    @EventListener(value = [BrokerAvailabilityEvent::class])
    fun onBrokerEvent(event: BrokerAvailabilityEvent) {
        isBrokerAvailable = event.isBrokerAvailable
        if (isBrokerAvailable) {
            connect()
        }
    }

    @Scheduled(fixedDelay = 5_000)
    fun reconnect() {
        if (!isConnected()) {
            logger.warn("Try to connect to broker")
            if (isBrokerAvailable) {
                connect()
            } else {
                val errorMessage = "Local sockJs client is disconnected, broker is down"
                logger.error(errorMessage)
                telegramNotificationService.sendMonitoring(errorMessage)
            }
        }
    }

    fun connect() {
        if (!isBrokerAvailable) {
            logger.error("Tried to connect, but broker is down")
        }
        try {
            this.clientSession =
                sockJsClient
                    .execute(
                        this,
                        WebSocketHttpHeaders(),
                        URI.create(appConfigProperties.localUrl)
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
        telegramNotificationService.sendMonitoring("Local sockJs client connected")
        val text =
            "CONNECT\naccept-version:1.1,1.0\nheart-beat:10000,10000\n\n\u0000"
        val connectMessage = TextMessage(text)
        session.sendMessage(connectMessage)
        logger.info("Sent message: '$connectMessage'")
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        telegramNotificationService.sendMonitoring("Local sockJs client connection closed with status $status")
        super.afterConnectionClosed(session, status)
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        telegramNotificationService.sendMonitoring(exception.toString(),
            TelegramNotificationService.buildTelegramDocumentDto(Exception(exception)))
        super.handleTransportError(session, exception)
    }

    fun sendTextMessage(message: TextMessage) {
        logger.trace("Got sendTextMessage: '$message'")
        clientSession?.sendMessage(message)
    }

    fun isConnected(): Boolean {
        return clientSession?.isOpen ?: false
    }
}