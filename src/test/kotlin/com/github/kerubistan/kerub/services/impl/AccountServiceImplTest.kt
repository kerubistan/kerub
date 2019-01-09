package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.AccountDao
import com.github.kerubistan.kerub.security.AssetAccessController
import com.nhaarman.mockito_kotlin.mock
import org.junit.AssumptionViolatedException
import org.junit.Test

class AccountServiceImplTest {

	private val dao = mock<AccountDao>()
	private val accessController = mock<AssetAccessController>()

	@Test
	fun search() {
		val service = AccountServiceImpl(dao, accessController)
		throw AssumptionViolatedException("not implemented")
	}

	@Test
	fun getById() {
		val service = AccountServiceImpl(dao, accessController)
		throw AssumptionViolatedException("not implemented")
	}
}