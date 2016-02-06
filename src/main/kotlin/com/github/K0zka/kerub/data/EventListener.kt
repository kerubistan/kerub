package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.messages.Message

interface EventListener {
	fun send(message: Message)
}