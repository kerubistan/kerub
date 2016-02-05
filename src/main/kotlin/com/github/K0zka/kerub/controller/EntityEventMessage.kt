package com.github.K0zka.kerub.controller

import com.github.K0zka.kerub.model.messages.EntityMessage
import java.io.Serializable

public data class EntityEventMessage(val message: EntityMessage) : Serializable