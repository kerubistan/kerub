package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.kerubistan.kroki.time.now
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import java.util.UUID
import kotlin.reflect.KClass

@JsonTypeName("project") data class Project @JsonCreator constructor(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID = UUID.randomUUID(),
		@Field
		@JsonProperty("name")
		override val name: String,
		@Field
		@JsonProperty("description")
		val description: String? = null,
		@Field
		@JsonProperty("created")
		val created: Long = now(),
		@Field
		@JsonProperty("expectations")
		override val expectations: List<Expectation> = listOf(),
		@Field
		@JsonProperty("quota")
		val quota: Quota? = null,
		override val owner: AssetOwner? = null
)
	: Entity<UUID>, Named, Constrained<Expectation>, Asset {
	override fun references(): Map<KClass<out Asset>, List<UUID>> = mapOf()
}
