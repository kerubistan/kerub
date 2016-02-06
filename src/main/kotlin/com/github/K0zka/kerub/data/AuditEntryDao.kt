package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.AuditEntry
import java.util.UUID

interface AuditEntryDao {
	fun add(entry: AuditEntry): UUID
	fun listById(id: UUID): List<AuditEntry>
}