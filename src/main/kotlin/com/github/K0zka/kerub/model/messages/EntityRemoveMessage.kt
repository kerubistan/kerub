package com.github.K0zka.kerub.model.messages

import com.github.K0zka.kerub.model.Entity
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonCreator

JsonTypeName("entity-remove")
JsonCreator
public class EntityRemoveMessage(obj : Entity<*>, date : Long) : EntityMessage(obj, date) {
}