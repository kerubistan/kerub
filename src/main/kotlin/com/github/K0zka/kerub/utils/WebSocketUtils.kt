package com.github.K0zka.kerub.utils

import org.springframework.web.socket.WebSocketSession

private val doubleQuote = "\""
private val seesionID = "JSESSIONID="

val WebSocketSession.servletSessionId: String
	get() =
	requireNotNull(this.handshakeHeaders["COOKIE"]?.firstOrNull { it.contains(seesionID) }
			?.substringAfter(seesionID)
			?.substringBefore(";")
			?.removePrefix(doubleQuote)
			?.removeSuffix(doubleQuote)) { "session ID should be set" }