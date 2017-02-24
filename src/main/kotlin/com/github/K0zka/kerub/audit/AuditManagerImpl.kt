package com.github.K0zka.kerub.audit

import com.github.K0zka.kerub.data.AuditEntryDao
import com.github.K0zka.kerub.model.AddEntry
import com.github.K0zka.kerub.model.DeleteEntry
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.model.UpdateEntry
import com.github.K0zka.kerub.utils.currentUser
import com.github.K0zka.kerub.utils.silent

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

	open internal fun getCurrentUser() = silent { currentUser() }
}