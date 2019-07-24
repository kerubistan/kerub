package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonInclude

data class User(
		val userName: String,
		@JsonInclude(JsonInclude.Include.NON_NULL)
		val quota: Quota? = null
)