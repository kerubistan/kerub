package com.github.K0zka.kerub.socket

import com.github.K0zka.kerub.model.messages.PingMessage
import com.github.K0zka.kerub.testWsUrl
import com.github.K0zka.kerub.utils.createObjectMapper
import com.github.K0zka.kerub.utils.getLogger
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import org.eclipse.jetty.websocket.client.WebSocketClient
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.URI
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class WebSocketSecurityIT {

	companion object {
		val logger = getLogger(WebSocketSecurityIT::class)
	}

	var socketClient: WebSocketClient? = null

	@Before
	fun setup() {
		socketClient = WebSocketClient()
		socketClient!!.start()
	}

	@After
	fun cleanup() {
		socketClient?.stop()
	}

	@Test
	fun unauthenticatedUser() {

		val queue: BlockingQueue<String> = ArrayBlockingQueue<String>(1024)

		@WebSocket
		class Listener {

			@OnWebSocketConnect
			fun connect(session: Session) {
				logger.info("connected: ${session.isOpen}")
				session.remote.sendString(
						createObjectMapper().writeValueAsString(PingMessage(sent = System.currentTimeMillis()))
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

		socketClient!!.connect(Listener(), URI(testWsUrl)).get()

		var messages = listOf<String>()
		var msg = queue.poll(1, TimeUnit.SECONDS)
		while (msg != null) {
			messages += msg
			msg = queue.poll(1, TimeUnit.SECONDS)
		}

		assertEquals(listOf("connected", "closed"), messages)
	}
}