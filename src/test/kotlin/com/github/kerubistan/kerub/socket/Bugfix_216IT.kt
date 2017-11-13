package com.github.kerubistan.kerub.socket

import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.createSocketClient
import com.github.kerubistan.kerub.login
import com.github.kerubistan.kerub.testWsUrl
import nl.komponents.kovenant.task
import org.eclipse.jetty.websocket.api.Session
import org.junit.Test
import java.net.URI
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.test.assertFalse

class Bugfix_216IT {

	@Test
	fun testMutlipleSockets() {
		fun poll(queue: BlockingQueue<String>): List<String> {
			var messages = listOf<String>()

			var msg = queue.poll(1, TimeUnit.SECONDS)
			while (msg != null) {
				messages += msg
				msg = queue.poll(1, TimeUnit.SECONDS)
			}
			return messages
		}

		fun sendTestMessages(session: Session) =
				task {
					for (i in 1..100) {
						session.remote.sendString(""" {"@type": "ping", "sent" : ${System.currentTimeMillis()}} """)
					}
				}


		val firstQueue: BlockingQueue<String> = ArrayBlockingQueue<String>(1024)
		val secondQueue: BlockingQueue<String> = ArrayBlockingQueue<String>(1024)

		val client = createClient()
		val rep = client.login("admin", "password")
		val firstClient = createSocketClient(rep)
		val firstSession = firstClient.connect(SocketListener(firstQueue), URI(testWsUrl)).get()
		sendTestMessages(firstSession)
		val secondClient = createSocketClient(rep)
		val secondSession = secondClient.connect(SocketListener(secondQueue), URI(testWsUrl)).get()
		sendTestMessages(secondSession)

		val firstQueueMessages = poll(firstQueue)
		val secondQueueMessages = poll(secondQueue)

		assertFalse("closed" in firstQueueMessages)
		assertFalse("closed" in secondQueueMessages)

	}

}