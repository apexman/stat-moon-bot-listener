package ru.apexman.statmoonbotlistener.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller
import ru.apexman.statmoonbotlistener.dto.TransactionDto
import ru.apexman.statmoonbotlistener.service.notification.TelegramNotificationService
import java.math.BigDecimal


@Controller
class MessageController(
    val mapper: ObjectMapper,
    val telegramNotificationService: TelegramNotificationService,
) {
    private val logger = LoggerFactory.getLogger(MessageController::class.java)

    @MessageMapping("/trades")
    fun broadcastTrades(@Payload message: String?) {
        logger.trace("trades message as string: '$message'")
        if (message != null) {
            val transactionDto = mapper.readValue(message, TransactionDto::class.java)
            logger.debug("$transactionDto")
            val filtered = transactionDto.filter { (it.profit?.compareTo(BigDecimal.valueOf(200L)) ?: -1) >= 0 }
            for (transactionDtoItem in filtered) {
                telegramNotificationService.trySendMessage(transactionDto.toString())
            }
        }
    }

    @MessageMapping("/sum")
    fun broadcastSum(@Payload message: String?) {
        logger.trace("sum message: '$message'")
    }

}