package com.github.kerubistan.kerub.controller

import com.github.kerubistan.kerub.model.messages.EntityMessage
import java.io.Serializable

data class EntityEventMessage(val message: EntityMessage) : Serializable