package ru.apexman.statmoonbotlistener.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppConfigProperties(
    val moonTradesUrl: String,
    val localUrl: String,
)