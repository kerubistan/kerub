package com.github.kerubistan.kerub.services.socket

interface InternalMessageListener {
	fun subscribe(sessionId: String, channel: String)

	fun unsubscribe(sessionId: String, channel: String)

	fun addSocketListener(id: String, conn: ClientConnection)

	fun removeSocketListener(id: String)
}