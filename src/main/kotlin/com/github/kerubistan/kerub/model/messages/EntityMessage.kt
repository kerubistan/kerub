package com.github.kerubistan.kerub.model.messages

import com.github.kerubistan.kerub.model.Entity

interface EntityMessage : Message {
	val obj: Entity<*>
	val date: Long
}
