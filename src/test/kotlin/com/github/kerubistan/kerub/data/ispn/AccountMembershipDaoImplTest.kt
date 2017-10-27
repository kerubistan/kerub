package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.model.AccountMembership
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AccountMembershipDaoImplTest : AbstractIspnDaoTest<UUID, AccountMembership>() {

	@Test
	fun isAccountMember() {
		val groupId = UUID.randomUUID()
		val dao = AccountMembershipDaoImpl(cache!!, eventListener, auditManager)
		dao.add(
				AccountMembership(
						user = "TEST",
						groupId = groupId,
						id = UUID.randomUUID()
				)
		)

		assertTrue { dao.isAccountMember("TEST", groupId) }
		assertFalse { dao.isAccountMember("TEST", UUID.randomUUID()) }
		assertFalse { dao.isAccountMember("SOME-OTHER-USER", groupId) }
	}

	@Test
	fun listByUsername() {
		val dao = AccountMembershipDaoImpl(cache!!, eventListener, auditManager)
		dao.add(
				AccountMembership(
						user = "TEST",
						groupId = UUID.randomUUID(),
						id = UUID.randomUUID()
				)
		)

		assertEquals(listOf<AccountMembership>(), dao.listByUsername("SOME-OTHER-USER"))
		assertEquals(1, dao.listByUsername("TEST").size)

	}

	@Test
	fun listByGroupId() {
		val dao = AccountMembershipDaoImpl(cache!!, eventListener, auditManager)
		val groupId = UUID.randomUUID()
		dao.add(
				AccountMembership(
						user = "TEST",
						groupId = groupId,
						id = UUID.randomUUID()
				)
		)

		assertEquals(listOf<AccountMembership>(), dao.listByGroupId(UUID.randomUUID()))
		assertEquals(1, dao.listByGroupId(groupId).size)
	}
}