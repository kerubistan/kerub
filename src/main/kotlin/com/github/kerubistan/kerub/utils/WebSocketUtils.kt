package com.github.kerubistan.kerub.utils

import org.springframework.web.socket.WebSocketSession

private const val doubleQuote = "\""
private const val sessionID = "JSESSIONID="

val WebSocketSession.servletSessionId: String
	get() =
	requireNotNull(this.handshakeHeaders["COOKIE"]?.firstOrNull { it.contains(sessionID) }
			?.substringAfter(sessionID)
			?.substringBefore(";")
			?.removePrefix(doubleQuote)
			?.removeSuffix(doubleQuote)) { "session ID should be set" }