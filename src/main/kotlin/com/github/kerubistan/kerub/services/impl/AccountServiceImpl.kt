package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.AccountDao
import com.github.kerubistan.kerub.model.Account
import com.github.kerubistan.kerub.model.paging.SearchResultPage
import com.github.kerubistan.kerub.security.AssetAccessController
import com.github.kerubistan.kerub.security.admin
import com.github.kerubistan.kerub.services.AccountService
import org.apache.shiro.SecurityUtils
import java.util.UUID

class AccountServiceImpl(
		override val dao: AccountDao,
		private val accessController: AssetAccessController
) : ListableBaseService<Account>("account"), AccountService {

	override fun search(field: String, value: String, start: Long, limit: Int): SearchResultPage<Account> {
		return if (SecurityUtils.getSubject().hasRole(admin)) {
			val list = dao.fieldSearch(field, value, start, limit)
			SearchResultPage(
					start = start,
					count = list.size.toLong(),
					result = list,
					searchby = field,
					total = list.size.toLong()
			)
		} else {
			TODO()
		}
	}

	override fun getById(id: UUID): Account =
			accessController.doAsAccountMember(id) { super.getById(id) }
}