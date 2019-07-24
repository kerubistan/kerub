package com.github.kerubistan.kerub.model

data class User(
		val userName: String,
		val quota: Quota? = null
)