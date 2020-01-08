package com.github.kerubistan.kerub.services.exc.mappers

import java.util.UUID

data class RestError(
		val code: String? = null,
		val message: String? = null,
		val uid: UUID? = null
)