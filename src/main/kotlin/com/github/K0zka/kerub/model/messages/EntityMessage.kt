package com.github.K0zka.kerub.model.messages

import com.github.K0zka.kerub.model.Entity

interface EntityMessage : Message {
	val obj: Entity<*>
	val date: Long
}
