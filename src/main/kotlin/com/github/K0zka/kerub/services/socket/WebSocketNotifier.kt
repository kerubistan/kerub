package com.github.K0zka.kerub.services.socket

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.K0zka.kerub.model.messages.EntityUpdateMessage
import com.github.K0zka.kerub.model.messages.Message
import com.github.K0zka.kerub.model.messages.PingMessage
import com.github.K0zka.kerub.model.messages.PongMessage
import com.github.K0zka.kerub.model.messages.SubscribeMessage
import com.github.K0zka.kerub.model.messages.UnsubscribeMessage
import com.github.K0zka.kerub.utils.getLogger
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.slf4j.Logger
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.StringWriter
import java.util.HashSet
import javax.websocket.Session

@RequiresAuthentication
public class WebSocketNotifier(val internalListener : InternalMessageListener) : TextWebSocketHandler() {
	protected companion object {
		private fun init() : ObjectMapper {
			val mapper = ObjectMapper()
			mapper.enableDefaultTyping()
			mapper.registerSubtypes(
					Message::class.java,
					SubscribeMessage::class.java,
					UnsubscribeMessage::class.java,
					EntityUpdateMessage::class.java,
					PingMessage::class.java,
					PongMessage::class.java)
			return mapper
		}
		val objectMapper = init()
		private val logger : Logger = getLogger(WebSocketNotifier::class)
	}

	fun send(session : WebSocketSession, message : Message) {
		StringWriter().use {
			objectMapper.writeValue(it, message)
			session.sendMessage( TextMessage( it.toString() ) )
		}
	}

	override fun handleTextMessage(session: WebSocketSession?, message: TextMessage?) {
		logger.info("text message {}",message)

		val msg = objectMapper.readValue<Message>(message?.getPayload(), Message::class.java)

		when(msg) {
			is PingMessage -> {
				send(session!!, PongMessage())
			}
			is SubscribeMessage -> {
				logger.info("subscribe to {}", msg.channel)
			}
			is UnsubscribeMessage -> {
				logger.info("unsubscribe from {}", msg.channel)
			}
		}
	}
	override fun handleTransportError(session: WebSocketSession, exception: Throwable?) {
		logger.info("connection error", exception)
	}
	@RequiresAuthentication
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