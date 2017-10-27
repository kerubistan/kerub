package com.github.kerubistan.kerub.model.messages

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("subscribe")
data class SubscribeMessage @JsonCreator constructor(
		@JsonProperty("channel") val channel: String
) : Message
