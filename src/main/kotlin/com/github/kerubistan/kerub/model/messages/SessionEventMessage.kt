package com.github.kerubistan.kerub.model.messages

data class SessionEventMessage(
		val closed: Boolean = true,
		val sessionId: String
) : Message