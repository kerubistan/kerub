package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.audit.AuditManager
import com.github.kerubistan.kerub.data.AccountDao
import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.model.Account
import org.infinispan.Cache
import java.util.UUID

class AccountDaoImpl(cache: Cache<UUID, Account>, eventListener: EventListener, auditManager: AuditManager)
	: AccountDao, ListableIspnDaoBase<Account, UUID>(cache, eventListener, auditManager) {
	override fun fieldSearch(field: String, value: String, start: Long, limit: Int): List<Account> =
			cache.fieldSearch(field = field, value = value, start = start, limit = limit)

	override fun getEntityClass(): Class<Account> = Account::class.java
}