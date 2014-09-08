package com.github.K0zka.kerub.services.socket

import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.OnClose
import javax.websocket.server.ServerEndpoint
import javax.websocket.OnMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.websocket.Decoder

ServerEndpoint("/ws",
               subprotocols = array("kerub"),
               decoders = array(javaClass<JacksonDecoder>()),
               encoders = array(javaClass<JacksonEncoder>()))
public class WebSocketNotifier {

	private final class object val logger : Logger = LoggerFactory.getLogger(javaClass())!!
	var session : Session? = null

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
	}

}