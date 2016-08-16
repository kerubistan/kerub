package com.github.K0zka.kerub.model

import java.util.UUID

data class Account(
		override val id: UUID,
		val name: String
) : Entity<UUID>