package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.AccountMembership
import java.util.UUID

interface AccountMembershipDao :CrudDao<AccountMembership, UUID> {
	fun listByGroupId(accountId : UUID) : List<AccountMembership>
	fun listByUsername(userName : String) : List<AccountMembership>
	fun isAccountMember(userName: String, accountId : UUID) : Boolean
}