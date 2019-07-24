package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.expectations.PoolExpectation
import java.util.UUID
import kotlin.reflect.KClass

/**
 * A pool of virtual machines based on a template.
 */
@JsonTypeName("pool")
data class Pool(
		override val id: UUID = UUID.randomUUID(),
		override val name: String,
		@JsonInclude(JsonInclude.Include.NON_NULL)
		override val owner: AssetOwner? = null,
		val templateId: UUID,
		override val expectations: List<PoolExpectation> = listOf()
) : Entity<UUID>, Named, Asset, Constrained<PoolExpectation> {
	override fun references(): Map<KClass<out Asset>, List<UUID>> =
			mapOf(Template::class to listOf(templateId))
}
