package com.github.kerubistan.kerub.model

import java.util.UUID

data class Icon(
		override val id: UUID,
		val mediaType: String,
		val data: ByteArray
) : Entity<UUID>