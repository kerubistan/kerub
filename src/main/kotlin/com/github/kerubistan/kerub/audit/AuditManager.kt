package com.github.kerubistan.kerub.audit

import com.github.kerubistan.kerub.model.Entity

interface AuditManager {
	fun auditUpdate(old: Entity<*>, new: Entity<*>)
	fun auditDelete(old: Entity<*>)
	fun auditAdd(new: Entity<*>)
}
