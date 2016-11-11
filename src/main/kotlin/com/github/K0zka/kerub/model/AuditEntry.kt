package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.search.annotations.Analyze
import org.hibernate.search.annotations.Field
import org.hibernate.search.annotations.Indexed
import java.util.UUID

/**
 * Represents a change in an entity. Audit entries should be created for each change on an entity, but never updated.
 */
@Indexed
interface AuditEntry : Entity<UUID> {
	val date: Long
	val user: String?
	val idStr: String
}


