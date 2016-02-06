package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.model.messages.EntityMessage

interface Planner {
	fun onEvent(msg: EntityMessage)
}