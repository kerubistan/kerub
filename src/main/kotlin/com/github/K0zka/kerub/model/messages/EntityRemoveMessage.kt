package com.github.K0zka.kerub.model.messages

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Entity

JsonTypeName("entity-remove")
JsonCreator
public data class EntityRemoveMessage(obj : Entity<*>, date : Long) : EntityMessage(obj, date)
