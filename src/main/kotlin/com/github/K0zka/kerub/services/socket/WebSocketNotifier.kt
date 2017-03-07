package com.github.K0zka.kerub.services.socket

import com.github.K0zka.kerub.model.messages.Message
import com.github.K0zka.kerub.model.messages.PingMessage
import com.github.K0zka.kerub.model.messages.PongMessage
import com.github.K0zka.kerub.model.messages.SubscribeMessage
import com.github.K0zka.kerub.model.messages.UnsubscribeMessage
import com.github.K0zka.kerub.security.EntityAccessController
import com.github.K0zka.kerub.utils.createObjectMapper
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.servletSessionId
import org.slf4j.Logger
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.StringWriter

class WebSocketNotifier(
		private val internalListener: InternalMessageListener,
		private val entityAccessController: EntityAccessController
) : TextWebSocketHandler() {
	companion object {
		private val objectMapper = createObjectMapper()
		private val logger: Logger = getLogger(WebSocketNotifier::class)
	}

	fun send(session: WebSocketSession, message: Message) {
		StringWriter().use {
			objectMapper.writeValue(it, message)
			session.sendMessage(TextMessage(it.toString()))
		}
	}

	override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
		logger.info("text message {}", message)

		val msg = objectMapper.readValue<Message>(message.payload, Message::class.java)

		when (msg) {
			is PingMessage -> {
				send(session, PongMessage())
			}
			is SubscribeMessage -> {
				logger.info("{} subscribe to {}", session.servletSessionId, msg.channel)
				internalListener.subscribe(session.servletSessionId, msg.channel)
			}
			is UnsubscribeMessage -> {
				logger.info("{} unsubscribe from {}", session.servletSessionId, msg.channel)
				internalListener.unsubscribe(session.servletSessionId, msg.channel)
			}
		}
	}

	override fun handleTransportError(session: WebSocketSession, exception: Throwable?) {
		logger.info("connection error", exception)
	}

	override fun afterConnectionEstablished(session: WebSocketSession) {
		if (session.principal == null) {
			logger.info("unauthenticated attempt")
			session.close(CloseStatus.NORMAL)
		} else {
			logger.info("connection opened by {}, {}", session.principal, session.servletSessionId)
			internalListener.addSocketListener(
					session.servletSessionId,
					SpringSocketClientConnection(session, objectMapper, entityAccessController)
			)
			super.afterConnectionEstablished(session)
		}
	}

	override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus?) {
		internalListener.removeSocketListener(session.servletSessionId)
		logger.info("connection closed, {}", status)
	}

}