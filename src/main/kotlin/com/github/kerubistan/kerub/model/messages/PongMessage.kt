package com.github.kerubistan.kerub.model.messages

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.utils.now

@JsonTypeName("pong")
data class PongMessage @JsonCreator constructor(
		@JsonProperty("sent") val sent: Long = now()
) : Message
