package com.github.K0zka.kerub.model

import java.util.UUID

data class Disk : Entity<UUID> {
	override var id: UUID? = null
	var size : Long = 0
	var expectations : List<Expectation> = listOf()
}