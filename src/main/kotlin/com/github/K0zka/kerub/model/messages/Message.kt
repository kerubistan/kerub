package com.github.K0zka.kerub.model.messages

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(EntityAddMessage::class),
		JsonSubTypes.Type(EntityRemoveMessage::class),
		JsonSubTypes.Type(EntityUpdateMessage::class),
		JsonSubTypes.Type(PingMessage::class),
		JsonSubTypes.Type(PongMessage::class),
		JsonSubTypes.Type(SubscribeMessage::class),
		JsonSubTypes.Type(UnsubscribeMessage::class)
)
interface Message : Serializable
