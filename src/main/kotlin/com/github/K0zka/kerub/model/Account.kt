package com.github.K0zka.kerub.model

import org.hibernate.search.annotations.Field
import java.util.UUID

data class Account(
		override val id: UUID,
		@Field
		val name: String,
		val requireProjects : Boolean = false,
		val quota : Quota? = null
) : Entity<UUID>