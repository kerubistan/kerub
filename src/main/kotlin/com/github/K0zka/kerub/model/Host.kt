package com.github.K0zka.kerub.model

import java.util.UUID

/**
 *
 */
data class Host : Entity<UUID> {
	override var id : UUID? = null

	var address : String? = null

	var dedicated : Boolean = true

}