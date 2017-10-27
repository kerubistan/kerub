package com.github.kerubistan.kerub.utils

import org.springframework.web.socket.WebSocketSession

private val doubleQuote = "\""
private val sessionID = "JSESSIONID="

val WebSocketSession.servletSessionId: String
	get() =
	requireNotNull(this.handshakeHeaders["COOKIE"]?.firstOrNull { it.contains(sessionID) }
			?.substringAfter(sessionID)
			?.substringBefore(";")
			?.removePrefix(doubleQuote)
			?.removeSuffix(doubleQuote)) { "session ID should be set" }