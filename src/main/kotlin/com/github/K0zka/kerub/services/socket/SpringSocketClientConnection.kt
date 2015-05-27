package com.github.K0zka.kerub.services.socket

import com.github.K0zka.kerub.model.messages.Message
import java.util.HashSet
import com.github.K0zka.kerub.model.messages.EntityUpdateMessage
import com.github.K0zka.kerub.utils.getLogger
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.TextMessage

public class SpringSocketClientConnection(val session : WebSocketSession, val mapper : ObjectMapper) : ClientConnection{
	private companion object val logger = getLogger(SpringSocketClientConnection::class)
	val subscriptions : MutableSet<String> = HashSet()
	fun removeSubscription(channel : String) {

	}
	override fun filterAndSend(msg: Message) {
		//TODO: filter first
		session.sendMessage(TextMessage(mapper.writeValueAsString(msg)))
	}
}