package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.model.messages.EntityMessage

public interface Planner {
	fun onEvent(msg: EntityMessage)
}