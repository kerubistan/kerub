package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.model.messages.Message

interface Planner {
	fun onEvent(msg: Message)
}