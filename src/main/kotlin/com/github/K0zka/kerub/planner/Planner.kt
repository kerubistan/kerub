package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.model.messages.Message

interface Planner {
	fun onEvent(msg: Message)
}