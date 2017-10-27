package com.github.kerubistan.kerub.services.socket

import com.github.kerubistan.kerub.model.messages.Message

interface ClientConnection {
	fun close()
	fun filterAndSend(msg: Message)
	fun removeSubscription(channel: String)
	fun addSubscription(channel: String)
}