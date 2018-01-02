package com.github.kerubistan.kerub.model.messages

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.utils.now

@JsonTypeName("entity-update")
data class EntityUpdateMessage @JsonCreator constructor(
		override val obj: Entity<*>,
		override val date: Long = now()
) : EntityMessage
