package com.github.K0zka.kerub.socket

import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.createSocketClient
import com.github.K0zka.kerub.login
import com.github.K0zka.kerub.logout
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
import kotlin.test.assertTrue

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


	@WebSocket
	class Listener(private val queue: BlockingQueue<String>) {

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

	@Test
	fun authenticatedUser() {
		val client = createClient()
		val rep = client.login("admin", "password")
		val socketClient = createSocketClient(rep)
		val queue: BlockingQueue<String> = ArrayBlockingQueue<String>(1024)

		val session = socketClient.connect(Listener(queue), URI(testWsUrl)).get()

		session.remote.sendString(
				createObjectMapper().writeValueAsString(PingMessage(sent = System.currentTimeMillis()))
		)

		client.logout()

		var messages = listOf<String>()
		var msg = queue.poll(1, TimeUnit.SECONDS)
		while (msg != null) {
			messages += msg
			msg = queue.poll(1, TimeUnit.SECONDS)
		}

		assertTrue(messages.first() == "connected")
		assertTrue(messages.last() == "closed")
		assertTrue(messages.contains("message"))

	}

	@Test
	fun unauthenticatedUser() {

		val queue: BlockingQueue<String> = ArrayBlockingQueue<String>(1024)

		socketClient!!.connect(Listener(queue), URI(testWsUrl)).get()

		var messages = listOf<String>()
		var msg = queue.poll(1, TimeUnit.SECONDS)
		while (msg != null) {
			messages += msg
			msg = queue.poll(1, TimeUnit.SECONDS)
		}

		assertEquals(listOf("connected", "closed"), messages)
	}
}