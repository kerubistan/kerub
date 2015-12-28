package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import org.hibernate.search.annotations.Indexed
import java.util.UUID

/**
 * Represents a change in an entity. Audit entries should be created for each change on an entity, but never updated.
 */
@Indexed
class AuditEntry(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID = UUID.randomUUID(),
		@Field
		val old: Entity<UUID>? = null,
		@Field
		val new: Entity<UUID>? = null,
		@Field
		val date: Long = System.currentTimeMillis(),
		@Field
		val user: UUID? = null,
		@Field
		val event: AuditEventType = AuditEventType.Update)
: Entity<UUID>
