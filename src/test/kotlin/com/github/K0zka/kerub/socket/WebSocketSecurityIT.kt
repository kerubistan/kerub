package com.github.K0zka.kerub.socket

import com.github.K0zka.kerub.testWsUrl
import com.github.K0zka.kerub.utils.getLogger
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import org.eclipse.jetty.websocket.client.WebSocketClient
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URI
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

class WebSocketSecurityIT {

	companion object {
		val logger = getLogger(WebSocketSecurityIT::class)
	}

	var socketClient : WebSocketClient? = null

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

		val messageReceived = AtomicBoolean(false)

		@WebSocket
		class Listener {

			@OnWebSocketConnect
			fun connect(session : Session) {
				logger.info("connected: ${session.isOpen}")
			}
			@OnWebSocketClose
			fun close(code: Int, msg : String?) {
				logger.info("connection closed {} {}", code, msg)
			}
			@OnWebSocketMessage
			fun message(session : Session, input : String) {
				logger.info("message: {}", input)
				messageReceived.set(true)
			}
			@OnWebSocketError
			fun error(error: Throwable) {
				logger.info("socket error", error)
			}
		}
		val session = socketClient!!.connect(Listener(), URI(testWsUrl)).get()
		session.use {
			session.remote.sendPing(ByteBuffer.wrap("hello".toByteArray(charset("UTF-8"))))

		}
		Assert.assertFalse(messageReceived.get())
	}
}