package com.github.kerubistan.kerub.services.socket

import com.github.kerubistan.kerub.model.messages.Message
import com.github.kerubistan.kerub.model.messages.PingMessage
import com.github.kerubistan.kerub.model.messages.PongMessage
import com.github.kerubistan.kerub.model.messages.SubscribeMessage
import com.github.kerubistan.kerub.model.messages.UnsubscribeMessage
import com.github.kerubistan.kerub.security.EntityAccessController
import com.github.kerubistan.kerub.utils.createObjectMapper
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.servletSessionId
import io.github.kerubistan.kroki.io.EasyStringWriter
import org.apache.shiro.subject.Subject
import org.slf4j.Logger
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

class WebSocketNotifier(
		private val internalListener: InternalMessageListener,
		private val entityAccessController: EntityAccessController
) : TextWebSocketHandler() {
	companion object {
		private val objectMapper = createObjectMapper()
		private val logger: Logger = getLogger()
	}

	fun send(session: WebSocketSession, message: Message) {
		EasyStringWriter().use {
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
				logger.info("{} / {} subscribe to {}", session.servletSessionId, session.id, msg.channel)
				internalListener.subscribe(session.servletSessionId, session.id, msg.channel)
			}
			is UnsubscribeMessage -> {
				logger.info("{} / {} unsubscribe from {}", session.servletSessionId, session.id, msg.channel)
				internalListener.unsubscribe(session.servletSessionId, session.id, msg.channel)
			}
		}
	}

	override fun handleTransportError(session: WebSocketSession, exception: Throwable?) {
		logger.info("connection error", exception)
	}

	override fun afterConnectionEstablished(session: WebSocketSession) {
		logger.info("connection opened by {}, {}", session.principal, session.servletSessionId)
		internalListener.addSocketListener(
				session.servletSessionId,
				session.id,
				SpringSocketClientConnection(
						session,
						objectMapper,
						entityAccessController,
						session.attributes["subject"] as Subject
				)
		)
		super.afterConnectionEstablished(session)
	}

	override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus?) {
		internalListener.removeSocketListener(session.servletSessionId)
		logger.info("connection closed, {}", status)
	}

}