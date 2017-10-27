package com.github.kerubistan.kerub.model.messages

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Entity

@JsonTypeName("entity-remove")
data class EntityRemoveMessage @JsonCreator constructor(
		override val obj: Entity<*>,
		override val date: Long = System.currentTimeMillis()
) : EntityMessage
