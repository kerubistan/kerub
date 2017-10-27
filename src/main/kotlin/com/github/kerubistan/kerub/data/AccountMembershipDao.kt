package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.AccountMembership
import java.util.UUID

interface AccountMembershipDao : CrudDao<AccountMembership, UUID> {
	fun listByGroupId(accountId: UUID): List<AccountMembership>
	fun listByUsername(userName: String): List<AccountMembership>
	fun isAccountMember(userName: String, accountId: UUID): Boolean
}