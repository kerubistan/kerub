package com.github.K0zka.kerub.services.socket

import com.github.K0zka.kerub.model.messages.Message

interface ClientConnection {
	fun filterAndSend(msg: Message)
	fun removeSubscription(channel: String)
	fun addSubscription(channel: String)
}