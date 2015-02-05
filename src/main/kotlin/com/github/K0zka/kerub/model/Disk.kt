package com.github.K0zka.kerub.model

import java.util.UUID

data class Disk(
		override val id: UUID,
		var size : Long,
		var expectations : List<Expectation> = serializableListOf()
               ) : Entity<UUID> {
}