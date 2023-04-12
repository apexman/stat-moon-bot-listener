package ru.apexman.statmoonbotlistener.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "telegram")
data class TelegramConfiguration(
    val enabled: Boolean,
    val telegramApiUrl: String,
    val token: String,
    val monitoringChatId: String,
    val isMonitoring: Boolean,
    val chatIds: Collection<String>,
)
