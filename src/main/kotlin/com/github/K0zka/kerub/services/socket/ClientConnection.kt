package com.github.K0zka.kerub.services.socket

import com.github.K0zka.kerub.model.messages.Message

public trait ClientConnection {
	fun filterAndSend(msg : Message)
}