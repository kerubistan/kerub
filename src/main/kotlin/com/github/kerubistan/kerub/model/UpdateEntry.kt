package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.utils.now
import org.hibernate.search.annotations.Analyze
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import java.util.UUID

@JsonTypeName("audit-update")
data class UpdateEntry(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID = UUID.randomUUID(),
		@Field
		@JsonProperty("date")
		override val date: Long = now(),
		@Field
		@JsonProperty("user")
		override val user: String?,
		@Field
		@JsonProperty("old")
		val old: Entity<*>,
		@Field
		@JsonProperty("new")
		val new: Entity<*>
) : AuditEntry {
	override val idStr: String
		@Field(analyze = Analyze.NO)
		@JsonIgnore
		get() = new.id.toString()
}