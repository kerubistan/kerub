package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.AuditEntry
import java.util.UUID

interface AuditEntryDao : DaoOperations.Add<AuditEntry, UUID> {
	fun listById(id: UUID): List<AuditEntry>
}