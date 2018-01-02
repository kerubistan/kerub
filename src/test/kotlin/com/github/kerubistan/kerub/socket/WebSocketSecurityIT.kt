package com.github.kerubistan.kerub.socket

import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.createSocketClient
import com.github.kerubistan.kerub.login
import com.github.kerubistan.kerub.logout
import com.github.kerubistan.kerub.model.messages.PingMessage
import com.github.kerubistan.kerub.testWsUrl
import com.github.kerubistan.kerub.utils.createObjectMapper
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.now
import org.eclipse.jetty.websocket.api.UpgradeException
import org.eclipse.jetty.websocket.client.WebSocketClient
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.URI
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

class WebSocketSecurityIT {

	companion object {
		private val logger = getLogger(WebSocketSecurityIT::class)
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
	fun authenticatedUser() {
		val client = createClient()
		val rep = client.login("admin", "password")
		val socketClient = createSocketClient(rep)
		val queue: BlockingQueue<String> = ArrayBlockingQueue<String>(1024)

		val session = socketClient.connect(SocketListener(queue), URI(testWsUrl)).get()

		session.remote.sendString(
				createObjectMapper().writeValueAsString(PingMessage(sent = now()))
		)

		var messages = listOf<String>()
		var msg = queue.poll(1, TimeUnit.SECONDS)
		while (msg != null) {
			messages += msg
			msg = queue.poll(1, TimeUnit.SECONDS)
		}

		assertTrue(messages.first() == "connected")
		assertTrue(messages.contains("message"))

		client.logout()

		msg = queue.poll(1, TimeUnit.SECONDS)
		while (msg != null) {
			messages += msg
			msg = queue.poll(1, TimeUnit.SECONDS)
		}

		assertTrue(messages.last() == "closed")

	}

	@Test
	fun unauthenticatedUser() {

		val queue: BlockingQueue<String> = ArrayBlockingQueue<String>(1024)

		com.github.kerubistan.kerub.expect(ExecutionException::class, {
			socketClient!!.connect(SocketListener(queue), URI(testWsUrl)).get()
		}, { it.cause is UpgradeException })

	}
}