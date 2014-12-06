package com.github.K0zka.kerub.model.controller

import java.util.UUID
import com.github.K0zka.kerub.model.Entity
import org.hibernate.search.annotations.Field

public data class Assignment : Entity<UUID> {
	override var id: UUID? = null
	Field
	var controller : String? = null
	Field
	var hostId : UUID? = null
}