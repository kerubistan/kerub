package com.github.K0zka.kerub.model.dynamic

import com.github.K0zka.kerub.model.Entity

public data class ControllerDynamic(val controllerId: String,
                                    val maxHosts: Int,
                                    val totalHosts: Int,
                                    val addresses: List<String>) : Entity<String> {
	override val id: String = controllerId
}