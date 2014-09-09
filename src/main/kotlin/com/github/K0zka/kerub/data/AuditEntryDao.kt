package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Entity
import java.util.UUID
import com.github.K0zka.kerub.model.AuditEntry

public trait AuditEntryDao {
	fun add(entry : AuditEntry) : UUID
	fun listById(id: UUID) : List<AuditEntry>
}