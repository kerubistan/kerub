package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.AccountMembershipDao
import com.github.K0zka.kerub.model.AccountMembership
import com.github.K0zka.kerub.services.AccountMembershipService
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