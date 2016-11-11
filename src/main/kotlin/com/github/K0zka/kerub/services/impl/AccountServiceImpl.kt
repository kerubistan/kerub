package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.AccountDao
import com.github.K0zka.kerub.model.Account
import com.github.K0zka.kerub.security.AccessController
import com.github.K0zka.kerub.services.AccountService
import java.util.UUID

class AccountServiceImpl(
		dao: AccountDao,
		private val accessController: AccessController
) : ListableBaseService<Account>(dao, "account"), AccountService {

	override fun getById(id: UUID): Account {
		return accessController.doAsAccountMember(id) { super.getById(id) }
	}
}