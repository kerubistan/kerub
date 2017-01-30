package com.github.K0zka.kerub.utils

import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.springframework.http.HttpHeaders
import org.springframework.web.socket.WebSocketSession
import kotlin.test.assertEquals

class WebSocketUtilsKtTest {

	val session: WebSocketSession = mock()
	val headers: HttpHeaders = mock()

	@Test
	fun servletSessionId() {
		whenever(session.handshakeHeaders).thenReturn(headers)
		whenever(headers[eq("COOKIE")]).thenReturn(listOf(
				"""rememberMe="deleteMe"; JSESSIONID="17s6x7x4c9is7191rh6ip5vqrt"; rememberMe="deleteMe"; JSESSIONID="17s6x7x4c9is7191rh6ip5vqrt""""
		))
		assertEquals("17s6x7x4c9is7191rh6ip5vqrt", session.servletSessionId)
	}

	@Test
	fun servletSessionIdSimple() {
		whenever(session.handshakeHeaders).thenReturn(headers)
		whenever(headers[eq("COOKIE")]).thenReturn(listOf(
				"""JSESSIONID=17ndx60fe3usktuwmbtq1bk5l"""
		))
		assertEquals("17ndx60fe3usktuwmbtq1bk5l", session.servletSessionId)
	}

}