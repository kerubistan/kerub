package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.audit.AuditManager
import com.github.kerubistan.kerub.data.AccountMembershipDao
import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.model.AccountMembership
import org.infinispan.Cache
import java.util.UUID

class AccountMembershipDaoImpl(cache: Cache<UUID, AccountMembership>,
							   eventListener: EventListener,
							   auditManager: AuditManager)
	: ListableIspnDaoBase<AccountMembership, UUID>(cache, eventListener, auditManager), AccountMembershipDao {
	override fun isAccountMember(userName: String, accountId: UUID): Boolean {
		val builder = cache.queryBuilder(AccountMembership::class)
		builder
				.having(AccountMembership::user.name).eq(userName)
				.and()
				.having(AccountMembership::groupIdStr.name).eq(accountId.toString())
		builder.maxResults(1)
		return builder.build().resultSize > 0
	}

	override fun listByUsername(userName: String): List<AccountMembership> {
		return search(AccountMembership::user.name, userName)
	}

	override fun getEntityClass(): Class<AccountMembership> = AccountMembership::class.java

	override fun listByGroupId(accountId: UUID): List<AccountMembership> {
		return search(AccountMembership::groupIdStr.name, accountId.toString())
	}

	private fun search(key: String, value: Any): List<AccountMembership> {
		val builder = cache.queryBuilder(AccountMembership::class)
		builder
				.having(key).eq(value)
		return builder.build().list<AccountMembership>()
	}

}