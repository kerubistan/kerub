package com.github.kerubistan.kerub.model.messages

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("session-event")
data class SessionEventMessage(
		val closed: Boolean = true,
		val sessionId: String
) : Message