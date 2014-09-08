package com.github.K0zka.kerub.services.socket

import javax.websocket.EndpointConfig
import org.slf4j.LoggerFactory
import javax.websocket.Decoder.Text
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.K0zka.kerub.services.socket.messages.Message
import com.github.K0zka.kerub.services.socket.messages.SubscribeMessage

public class JacksonDecoder : Text<Message>, JacksonBase() {
	override fun decode(s: String?): Message? {
		return objectMapper.reader(javaClass<Message>())!!.readValue<Message>(s)
	}
	override fun willDecode(s: String?): Boolean {
		return true
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