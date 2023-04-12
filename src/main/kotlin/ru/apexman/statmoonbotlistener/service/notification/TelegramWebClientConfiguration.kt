package ru.apexman.statmoonbotlistener.service.notification

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.DefaultUriBuilderFactory
import reactor.core.publisher.Mono
import ru.apexman.statmoonbotlistener.config.TelegramConfiguration

@Configuration
class TelegramWebClientConfiguration {

    private val logger = LoggerFactory.getLogger(TelegramWebClientConfiguration::class.java)

    @Bean
    fun telegramWebClient(telegramConfiguration: TelegramConfiguration): WebClient {
        val factory = DefaultUriBuilderFactory(telegramConfiguration.telegramApiUrl)
        factory.encodingMode = DefaultUriBuilderFactory.EncodingMode.NONE
        return WebClient
            .builder()
            .baseUrl(telegramConfiguration.telegramApiUrl)
            .uriBuilderFactory(factory)
            .filters { exchangeFilterFunctions ->
                exchangeFilterFunctions.add(requestLog())
                exchangeFilterFunctions.add(responseLog())
            }
            .build()
    }

    fun requestLog(): ExchangeFilterFunction =
        ExchangeFilterFunction.ofRequestProcessor {
            logger.debug("Requesting ${it.method()}: ${it.url()}")
            Mono.just(it)
        }

    fun responseLog(): ExchangeFilterFunction =
        ExchangeFilterFunction.ofResponseProcessor {
            logger.debug("Got response with status code: ${it.statusCode()}")
            Mono.just(it)
        }
}
