package com.github.K0zka.kerub.services.socket

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import org.springframework.web.socket.WebSocketSession

@RunWith(MockitoJUnitRunner::class)
class SpringSocketClientConnectionTest {

	@Mock
	var session : WebSocketSession? = null

	@Mock
	var mapper : ObjectMapper? = null

	@Before
	fun setUp() {

	}

	@Test
	fun removeSubscription() {

	}

	@Test
	fun addSubscription() {

	}

	@Test
	fun filterAndSend() {

	}
}