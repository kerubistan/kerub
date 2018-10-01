package com.github.kerubistan.kerub.audit

import com.github.kerubistan.kerub.data.AuditEntryDao
import com.github.kerubistan.kerub.model.AddEntry
import com.github.kerubistan.kerub.model.DeleteEntry
import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.UpdateEntry
import com.github.kerubistan.kerub.utils.LogLevel
import com.github.kerubistan.kerub.utils.currentUser
import com.github.kerubistan.kerub.utils.silent

open class AuditManagerImpl(private val auditEntryDao: AuditEntryDao) : AuditManager {
	override fun auditUpdate(old: Entity<*>, new: Entity<*>) {
		auditEntryDao.add(
				UpdateEntry(
						old = old,
						new = new,
						user = getCurrentUser()
				)
		)
	}

	override fun auditDelete(old: Entity<*>) {
		auditEntryDao.add(
				DeleteEntry(
						old = old,
						user = getCurrentUser()
				)
		)
	}

	override fun auditAdd(new: Entity<*>) {
		auditEntryDao.add(
				AddEntry(
						new = new,
						user = getCurrentUser()
				)
		)
	}

	internal open fun getCurrentUser() = silent(level = LogLevel.Off) { currentUser() }
}