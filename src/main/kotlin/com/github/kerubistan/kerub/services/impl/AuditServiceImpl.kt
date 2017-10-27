package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.AuditEntryDao
import com.github.kerubistan.kerub.model.AuditEntry
import com.github.kerubistan.kerub.services.AuditService
import java.util.UUID

class AuditServiceImpl(val auditEntryDao: AuditEntryDao) : AuditService {
	override fun listById(id: UUID): List<AuditEntry> {
		return auditEntryDao.listById(id)
	}
}