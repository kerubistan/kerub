package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonView
import com.github.kerubistan.kerub.model.expectations.VirtualNetworkExpectation
import com.github.kerubistan.kerub.model.views.Simple
import org.hibernate.search.annotations.Field
import java.util.UUID
import kotlin.reflect.KClass

@JsonTypeName("vnet")
data class VirtualNetwork(
		override val id: UUID,
		override val expectations: List<VirtualNetworkExpectation> = listOf(),
		override val name: String,

		@Field
		@JsonView(Simple::class)
		@JsonProperty("owner")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		override val owner: AssetOwner? = null

) : Entity<UUID>, Constrained<VirtualNetworkExpectation>, Asset {
	override fun references(): Map<KClass<out Asset>, List<UUID>> =
			mapOf()
}