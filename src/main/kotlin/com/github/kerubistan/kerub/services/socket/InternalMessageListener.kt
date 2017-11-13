package com.github.kerubistan.kerub.services.socket

interface InternalMessageListener {
	fun subscribe(sessionId: String, socketId: String, channel: String)

	fun unsubscribe(sessionId: String, socketId: String, channel: String)

	fun addSocketListener(sessionId: String, socketId: String, conn: ClientConnection)

	fun removeSocketListener(sessionId: String)
}