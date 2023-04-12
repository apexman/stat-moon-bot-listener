package ru.apexman.statmoonbotlistener.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.ZonedDateTime

class TransactionDto : ArrayList<TransactionDtoItem>()

data class TransactionDtoItem(
    @JsonProperty("b")
    val buyPrice: BigDecimal?,
    @JsonProperty("bi")
    val userId: BigDecimal?,
    @JsonProperty("c")
    val coin: String?,
    @JsonProperty("date")
    val date: Any?,
    @JsonProperty("o")
    val orderPrice: BigDecimal?,
    @JsonProperty("p")
    val profit: BigDecimal?,
    @JsonProperty("s")
    val sellPrice: BigDecimal?,
    @JsonProperty("t")
    val telegramUsername: String?,
    @JsonProperty("u")
    val profitAbsolute: BigDecimal?,
    @JsonProperty("a")
    val a: BigDecimal?,
    @JsonProperty("e")
    val e: BigDecimal?,
    @JsonProperty("f")
    val f: BigDecimal?,
    @JsonProperty("ft")
    val ft: BigDecimal?,
    @JsonProperty("i")
    val i: BigDecimal?,
    @JsonProperty("l")
    val time: ZonedDateTime?,
    @JsonProperty("m")
    val m: BigDecimal?,
    @JsonProperty("sp")
    val sp: BigDecimal?,
    @JsonProperty("pb")
    val pb: BigDecimal?,
)