package com.github.K0zka.kerub.services.socket

import javax.websocket.Session
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.github.K0zka.kerub.model.messages.PingMessage
import com.github.K0zka.kerub.model.messages.PongMessage
import com.github.K0zka.kerub.model.messages.SubscribeMessage
import com.github.K0zka.kerub.model.messages.UnsubscribeMessage
import java.util.HashSet
import com.github.K0zka.kerub.model.messages.Message
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.CloseStatus
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.K0zka.kerub.model.messages.EntityUpdateMessage
import java.io.StringWriter

public class WebSocketNotifier(val internalListener : InternalMessageListener) : TextWebSocketHandler() {
	protected companion object {
		private fun init() : ObjectMapper {
			val mapper = ObjectMapper()
			mapper.enableDefaultTyping()
			mapper.registerSubtypes(
					javaClass<Message>(),
					javaClass<SubscribeMessage>(),
					javaClass<UnsubscribeMessage>(),
					javaClass<EntityUpdateMessage>(),
					javaClass<PingMessage>(),
					javaClass<PongMessage>())
			return mapper
		}
		val objectMapper = init()
		val logger : Logger = LoggerFactory.getLogger(javaClass<WebSocketNotifier>())!!
	}

	fun send(session : WebSocketSession, message : Message) {
		StringWriter().use {
			objectMapper.writeValue(it, message)
			session.sendMessage( TextMessage( it.toString() ) )
		}
	}

	override fun handleTextMessage(session: WebSocketSession?, message: TextMessage?) {
		logger.info("text message {}",message)

		val msg = objectMapper.readValue<Message>(message?.getPayload(), javaClass<Message>())

		when(msg) {
			is PingMessage -> {
				send(session!!, PongMessage())
			}
			is SubscribeMessage -> {
				logger.info("subscribe to {}", (msg : SubscribeMessage).channel)

			}
			is UnsubscribeMessage -> {
				logger.info("unsubscribe from {}", (msg : UnsubscribeMessage).channel)
			}
		}
	}
	override fun handleTransportError(session: WebSocketSession, exception: Throwable?) {
		logger.info("connection error", exception)
	}
	override fun afterConnectionEstablished(session: WebSocketSession) {
		logger.info("connection opened")
		internalListener.addSocketListener(session.getId(), SpringSocketClientConnection(session, objectMapper))
		super.afterConnectionEstablished(session)
	}
	override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus?) {
		internalListener.removeSocketListener(session.getId())
		logger.info("connection closed, {}", status)
	}

	private var session : Session? = null
	private val clients : MutableSet<ClientConnection> = HashSet()

}