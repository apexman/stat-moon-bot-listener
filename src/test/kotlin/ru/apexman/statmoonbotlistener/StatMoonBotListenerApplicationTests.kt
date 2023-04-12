package ru.apexman.statmoonbotlistener

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.time.TimeZones
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.apexman.statmoonbotlistener.dto.TransactionDto
import java.time.*
import java.util.TimeZone

@SpringBootTest
class StatMoonBotListenerApplicationTests(
	@Autowired val mapper: ObjectMapper
) {

	@Test
	fun contextLoads() {
		val text = """
			[{"date":null,"i":16059281,"b":0.04512,"s":0.0453,"o":6.68,"a":3,"f":1,"pb":1,"e":4,"sp":85,"l":1681305111,"c":"ARPA","ft":0,"u":0.34,"bi":404,"t":"@moon_bot_kurilka","m":0,"p":5.09}]
		""".trimIndent()
		val readValue = mapper.readValue(text, TransactionDto::class.java)
		println(readValue)
		println(readValue[0].time)
	}

}
