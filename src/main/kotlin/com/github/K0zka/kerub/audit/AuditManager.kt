package com.github.K0zka.kerub.audit

import com.github.K0zka.kerub.model.Entity

interface AuditManager {
	fun auditUpdate(old: Entity<*>, new: Entity<*>)
	fun auditDelete(old: Entity<*>)
	fun auditAdd(new: Entity<*>)
}
