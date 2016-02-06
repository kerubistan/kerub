package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.K0zka.kerub.model.Entity
import org.hibernate.search.annotations.DocumentId

data class ControllerDynamic(val controllerId: String,
									val maxHosts: Int,
									val totalHosts: Int,
									val addresses: List<String>) : Entity<String> {
	@DocumentId
	@JsonProperty("id")
	override val id: String = controllerId
}