package com.github.K0zka.kerub.services.socket

import com.github.K0zka.kerub.model.messages.Message
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import org.springframework.web.socket.WebSocketSession

@RunWith(MockitoJUnitRunner::class)
class WebSocketNotifierTest {

	var internal : InternalMessageListener? = null

	@Mock
	var session : WebSocketSession? = null

	@Mock
	var connection : ClientConnection? = null

	var notifier : WebSocketNotifier? = null

	@Before
	fun setup() {
		internal = InternalMessageListener()
		notifier = WebSocketNotifier(internal!!)
	}

	@Test
	fun afterConnectionEstablished() {
		Mockito.`when`(session!!.id).thenReturn("session-id")

		notifier!!.afterConnectionEstablished(session!!)

		//TODO
	}
}