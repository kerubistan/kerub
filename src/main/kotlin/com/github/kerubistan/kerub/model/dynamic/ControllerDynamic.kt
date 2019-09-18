package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Entity
import org.hibernate.search.annotations.DocumentId

@JsonTypeName("controller-dynamic")
data class ControllerDynamic(val controllerId: String,
							 val maxHosts: Int,
							 val totalHosts: Int,
							 val addresses: List<String>) : Entity<String> {
	@DocumentId
	@JsonProperty("id")
	override val id: String = controllerId
}