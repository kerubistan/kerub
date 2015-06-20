package com.github.K0zka.kerub.model.messages

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Entity

JsonTypeName("entity-add")
JsonCreator
public class EntityAddMessage(obj : Entity<*>, date : Long) : EntityMessage(obj, date)
