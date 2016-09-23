package com.github.K0zka.kerub.services.socket

import com.github.K0zka.kerub.planner.Planner
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.security.Principal

@RunWith(MockitoJUnitRunner::class)
class WebSocketNotifierTest {

	@Mock
	var planner: Planner? = null

	@Mock
	var internal: InternalMessageListener? = null

	@Mock
	var session: WebSocketSession? = null

	@Mock
	var connection: ClientConnection? = null

	@Mock
	var principal: Principal? = null

	var notifier: WebSocketNotifier? = null

	@Before
	fun setup() {
		notifier = WebSocketNotifier(internal!!)
	}

	@Test
	fun afterConnectionEstablished() {
		Mockito.`when`(session!!.id).thenReturn("session-id")
		Mockito.`when`(session!!.principal).thenReturn(principal)

		notifier!!.afterConnectionEstablished(session!!)

		val conn = Mockito.mock(ClientConnection::class.java)
		//		verify(internal)!!.addSocketListener(
		//				anyString(),
		//				Matchers.any(ClientConnection::class.java) ?: conn
		//		)
	}

	@Test
	fun handleTextMessageWithPing() {
		notifier!!.handleMessage(session, TextMessage(
				"""{
				"@type":"ping",
				"sent":${System.currentTimeMillis()}
				}"""
		))

		verify(session)!!.sendMessage(Matchers.any(TextMessage::class.java) ?: TextMessage(""))
	}

	@Test
	fun handleTextMessageWithSubscribe() {
		Mockito.`when`(session!!.id).thenReturn("test-session-id")
		notifier!!.handleMessage(session, TextMessage(
				"""{
				"@type":"subscribe",
				"channel":"/host/"
				}"""
		))

	}

	@Test
	fun handleTextMessageWithUnsubscribe() {
		Mockito.`when`(session!!.id).thenReturn("test-session-id")
		notifier!!.handleMessage(session, TextMessage(
				"""{
				"@type":"unsubscribe",
				"channel":"/host/"
				}"""
		))


	}

}