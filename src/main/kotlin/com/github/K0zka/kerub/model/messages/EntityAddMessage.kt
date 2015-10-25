package com.github.K0zka.kerub.model.messages

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Entity

@JsonTypeName("entity-add")
public data class EntityAddMessage @JsonCreator constructor(override val obj: Entity<*>, override val date: Long) : EntityMessage
