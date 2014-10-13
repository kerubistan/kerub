package com.github.K0zka.kerub.model.messages

import com.github.K0zka.kerub.model.Entity

public open class EntityMessage(val obj : Entity<*>, val date : Long) : Message {

}