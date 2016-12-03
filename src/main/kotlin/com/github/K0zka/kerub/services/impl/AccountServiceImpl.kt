package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.AccountDao
import com.github.K0zka.kerub.model.Account
import com.github.K0zka.kerub.model.paging.SearchResultPage
import com.github.K0zka.kerub.security.AccessController
import com.github.K0zka.kerub.services.AccountService
import java.util.UUID

class AccountServiceImpl(
		override val dao: AccountDao,
		private val accessController: AccessController
) : ListableBaseService<Account>("account"), AccountService {

	override fun search(field: String, value: String, start: Long, limit: Int): SearchResultPage<Account> {
		TODO()
	}

	override fun getById(id: UUID): Account =
		accessController.doAsAccountMember(id) { super.getById(id) }
}