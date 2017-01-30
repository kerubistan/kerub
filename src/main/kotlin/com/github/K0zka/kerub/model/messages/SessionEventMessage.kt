package com.github.K0zka.kerub.model.messages

data class SessionEventMessage(
		val closed: Boolean = true,
		val sessionId: String
) : Message