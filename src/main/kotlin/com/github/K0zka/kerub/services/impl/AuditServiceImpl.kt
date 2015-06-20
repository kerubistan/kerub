package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.AuditEntryDao
import com.github.K0zka.kerub.model.AuditEntry
import com.github.K0zka.kerub.services.AuditService
import java.util.UUID

public class AuditServiceImpl(val auditEntryDao : AuditEntryDao) : AuditService {
	override fun listById(id: UUID) : List<AuditEntry> {
		return auditEntryDao.listById(id)
	}
}