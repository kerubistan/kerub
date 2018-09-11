package com.github.kerubistan.kerub.services.socket

import com.github.kerubistan.kerub.security.EntityAccessController
import com.github.kerubistan.kerub.utils.now
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.shiro.subject.Subject
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.springframework.http.HttpHeaders
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.security.Principal

class WebSocketNotifierTest {

	private val internal: InternalMessageListener = mock()
	private val session: WebSocketSession = mock()
	private val headers: HttpHeaders = mock()
	private val principal: Principal = mock()
	private val entityAccessController: EntityAccessController = mock()

	private var notifier: WebSocketNotifier? = null

	@Before
	fun setup() {
		notifier = WebSocketNotifier(internal, entityAccessController)
		whenever(session.handshakeHeaders).thenReturn(headers)
		whenever(headers[eq("COOKIE")]).thenReturn(listOf("JSESSIONID=test-session-id"))
	}

	@Test
	fun afterConnectionEstablished() {
		whenever(session.id).thenReturn("socket-id")
		whenever(session.principal).thenReturn(principal)
		whenever(session.attributes).thenReturn(mapOf("subject" to mock<Subject>()))

		notifier!!.afterConnectionEstablished(session)

		verify(internal).addSocketListener(
				anyString(),
				eq("socket-id"),
				any()
		)
	}

	@Test
	fun handleTextMessageWithPing() {
		notifier!!.handleMessage(session, TextMessage(
				"""{
				"@type":"ping",
				"sent":${now()}
				}"""
		))

		verify(session).sendMessage(any<TextMessage>())
	}

	@Test
	fun handleTextMessageWithSubscribe() {
		whenever(session.id).thenReturn("test-session-id")
		notifier!!.handleMessage(session, TextMessage(
				"""{
				"@type":"subscribe",
				"channel":"/host/"
				}"""
		))

	}

	@Test
	fun handleTextMessageWithUnsubscribe() {
		whenever(session.id).thenReturn("test-session-id")
		notifier!!.handleMessage(session, TextMessage(
				"""{
				"@type":"unsubscribe",
				"channel":"/host/"
				}"""
		))

	}

}