package com.github.kerubistan.kerub.socket

import com.github.kerubistan.kerub.model.messages.PingMessage
import com.github.kerubistan.kerub.utils.createObjectMapper
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.now
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import java.util.concurrent.BlockingQueue

@WebSocket
class SocketListener(private val queue: BlockingQueue<String>) {

	companion object {
		private val logger = getLogger(SocketListener::class)
	}

	@OnWebSocketConnect
	fun connect(session: Session) {
		logger.info("connected: ${session.isOpen}")
		session.remote.sendString(
				createObjectMapper().writeValueAsString(PingMessage(sent = now()))
		)
		queue.put("connected")
	}

	@OnWebSocketClose
	fun close(code: Int, msg: String?) {
		logger.info("connection closed {} {}", code, msg)
		queue.put("closed")
	}

	@OnWebSocketMessage
	fun message(session: Session, input: String) {
		logger.info("message: {}", input)
		queue.put("message")
	}

	@OnWebSocketError
	fun error(error: Throwable) {
		logger.info("socket error", error)
		queue.put("error")
	}
}