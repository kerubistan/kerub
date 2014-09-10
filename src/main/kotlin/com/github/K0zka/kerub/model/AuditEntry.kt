package com.github.K0zka.kerub.model

import java.util.UUID
import org.hibernate.search.annotations.Indexed
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field

Indexed
class AuditEntry(
		Field
		val old : Entity<UUID>? = null,
		Field
		val new : Entity<UUID>? = null,
		Field
		val date : Long = System.currentTimeMillis(),
		Field
		val user : UUID? = null,
		Field
		val event : AuditEventType = AuditEventType.Update) : Entity<UUID> {
	DocumentId
	override var id: UUID? = null
}