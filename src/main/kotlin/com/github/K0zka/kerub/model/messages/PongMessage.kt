package com.github.K0zka.kerub.model.messages

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("pong")
data class PongMessage @JsonCreator constructor(
		@JsonProperty("sent") val sent: Long = System.currentTimeMillis()
) : Message
