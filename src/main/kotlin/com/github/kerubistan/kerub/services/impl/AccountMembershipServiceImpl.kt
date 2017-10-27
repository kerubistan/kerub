package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.AccountMembershipDao
import com.github.kerubistan.kerub.model.AccountMembership
import com.github.kerubistan.kerub.services.AccountMembershipService
import java.util.UUID

class AccountMembershipServiceImpl(private val dao: AccountMembershipDao) : AccountMembershipService {
	override fun listUserAccounts(userName: String) =
			dao.listByUsername(userName)

	override fun add(accountId: UUID, membershipId: UUID, accountMembership: AccountMembership) {
		dao.add(accountMembership.copy(
				groupId = accountId,
				id = membershipId
		))
	}

	override fun remove(accountId: UUID, membershipId: UUID) {
		dao.remove(membershipId)
	}

	override fun list(accountId: UUID): List<AccountMembership>
			= dao.listByGroupId(accountId)
}