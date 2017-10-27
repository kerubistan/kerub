package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.messages.Message

interface EventListener {
	fun send(message: Message)
}