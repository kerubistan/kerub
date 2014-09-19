package com.github.K0zka.kerub.services.socket

import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.OnClose
import javax.websocket.server.ServerEndpoint
import javax.websocket.OnMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.websocket.Decoder
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.services.socket.messages.EntityUpdateMessage
import com.github.K0zka.kerub.services.socket.messages.PingMessage
import com.github.K0zka.kerub.services.socket.messages.PongMessage
import com.github.K0zka.kerub.services.socket.messages.SubscribeMessage
import com.github.K0zka.kerub.services.socket.messages.UnsubscribeMessage
import java.util.LinkedList
import java.util.HashSet

ServerEndpoint("/ws",
               subprotocols = array("kerub"),
               decoders = array(javaClass<JacksonDecoder>()),
               encoders = array(javaClass<JacksonEncoder>()))
public class WebSocketNotifier {

	private final class object val logger : Logger = LoggerFactory.getLogger(javaClass<WebSocketNotifier>())!!
	private var session : Session? = null
	private val subscriptions : MutableSet<String> = HashSet()

	OnOpen
	fun open(session : Session) {
		this.session = session
		logger.debug("connection opened")
	}

	OnClose
	fun close() {
		logger.debug("connection closed")
	}

	OnMessage()
	fun message(message : Any?) {
		logger.debug("message from user: $message")
		//handle message
		when(message) {
			is PingMessage ->
				session
						?.getAsyncRemote()
						?.sendObject( PongMessage() )
			is SubscribeMessage -> {
				logger.info("subscribe to {}", (message as SubscribeMessage).channel)
				this.subscriptions.add((message as SubscribeMessage).channel)
			}
			is UnsubscribeMessage -> {
				logger.info("unsubscribe from {}", (message as UnsubscribeMessage).channel)
				this.subscriptions.remove((message as UnsubscribeMessage).channel)
			}
		}
	}

	fun onUpdate(obj : Entity<Any>, sent : Long) {
		session
				?.getAsyncRemote()
				?.sendObject( EntityUpdateMessage(date = sent, obj = obj) )
	}

}