package com.github.K0zka.kerub.services.socket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping
import com.github.K0zka.kerub.services.socket.messages.Message
import com.github.K0zka.kerub.services.socket.messages.SubscribeMessage
import com.github.K0zka.kerub.services.socket.messages.UnsubscribeMessage
import com.github.K0zka.kerub.services.socket.messages.EntityUpdateMessage
import com.github.K0zka.kerub.services.socket.messages.PingMessage

fun init() : ObjectMapper {
	val mapper = ObjectMapper()
	mapper.enableDefaultTyping()
	mapper.registerSubtypes(
			javaClass<Message>(),
			javaClass<SubscribeMessage>(),
			javaClass<UnsubscribeMessage>(),
			javaClass<EntityUpdateMessage>(),
			javaClass<PingMessage>())
	return mapper
}

public open class JacksonBase {
	protected class object
		val objectMapper = init()
}