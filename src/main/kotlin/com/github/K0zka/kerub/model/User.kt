package com.github.K0zka.kerub.model

data class User (
		val userName : String,
		val quota: Quota? = null
)