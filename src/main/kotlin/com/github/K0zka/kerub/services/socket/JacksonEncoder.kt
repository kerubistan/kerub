package com.github.K0zka.kerub.services.socket

import javax.websocket.EndpointConfig
import javax.websocket.Encoder.Text
import org.slf4j.LoggerFactory
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.StringWriter

public class JacksonEncoder : Text<Any>, JacksonBase() {
	override fun encode(`object`: Any?): String? {
		val writer = StringWriter()
		objectMapper.writeValue(writer, `object`)
		return writer.toString()
	}

	private class object
	val logger = LoggerFactory.getLogger(javaClass())!!;
	override fun init(config: EndpointConfig?) {
		logger.debug("decoder initialized")
	}
	override fun destroy() {
		logger.debug("decoder destroyed")
	}
}