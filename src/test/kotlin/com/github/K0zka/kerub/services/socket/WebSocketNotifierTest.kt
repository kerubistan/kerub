package com.github.K0zka.kerub.services.socket

import com.github.K0zka.kerub.planner.Planner
import com.github.K0zka.kerub.security.EntityAccessController
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.shiro.subject.Subject
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.springframework.http.HttpHeaders
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.security.Principal

class WebSocketNotifierTest {

	val planner: Planner = mock()
	val internal: InternalMessageListener = mock()
	val session: WebSocketSession = mock()
	val headers: HttpHeaders = mock()
	val principal: Principal = mock()
	val entityAccessController: EntityAccessController = mock()

	var notifier: WebSocketNotifier? = null

	@Before
	fun setup() {
		notifier = WebSocketNotifier(internal, entityAccessController)
		whenever(session.handshakeHeaders).thenReturn(headers)
		whenever(headers.get(eq("COOKIE"))).thenReturn(listOf("JSESSIONID=test-session-id"))
	}

	@Test
	fun afterConnectionEstablished() {
		whenever(session.id).thenReturn("session-id")
		whenever(session.principal).thenReturn(principal)
		whenever(session.attributes).thenReturn(mapOf("subject" to mock<Subject>()))

		notifier!!.afterConnectionEstablished(session)

		verify(internal).addSocketListener(
				anyString(),
				any<ClientConnection>()
		)
	}

	@Test
	fun handleTextMessageWithPing() {
		notifier!!.handleMessage(session, TextMessage(
				"""{
				"@type":"ping",
				"sent":${System.currentTimeMillis()}
				}"""
		))

		verify(session).sendMessage(any<TextMessage>())
	}

	@Test
	fun handleTextMessageWithSubscribe() {
		Mockito.`when`(session.id).thenReturn("test-session-id")
		notifier!!.handleMessage(session, TextMessage(
				"""{
				"@type":"subscribe",
				"channel":"/host/"
				}"""
		))

	}

	@Test
	fun handleTextMessageWithUnsubscribe() {
		Mockito.`when`(session.id).thenReturn("test-session-id")
		notifier!!.handleMessage(session, TextMessage(
				"""{
				"@type":"unsubscribe",
				"channel":"/host/"
				}"""
		))

	}

}