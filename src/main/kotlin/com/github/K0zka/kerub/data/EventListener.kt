package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.messages.Message

public interface EventListener {
	fun send(message: Message)
}