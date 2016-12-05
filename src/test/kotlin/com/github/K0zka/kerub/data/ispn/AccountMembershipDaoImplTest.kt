package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.AccountMembershipDao
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.model.AccountMembership
import com.nhaarman.mockito_kotlin.mock
import org.infinispan.Cache
import org.infinispan.manager.DefaultCacheManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AccountMembershipDaoImplTest {

	var cacheManager: DefaultCacheManager? = null
	var cache: Cache<UUID, AccountMembership>? = null
	var dao: AccountMembershipDao? = null
	var eventListener: EventListener = mock()

	val auditManager = mock<AuditManager>()


	@Before
	fun setup() {
		cacheManager = DefaultCacheManager()
		cacheManager!!.start()
		cache = cacheManager!!.getCache("test")
		cache!!.clear()
		dao = AccountMembershipDaoImpl(cache!!, eventListener, auditManager)
	}

	@After fun cleanup() {
		cache?.clear()
		cacheManager?.stop()
	}

	@Test
	fun isAccountMember() {
		val groupId = UUID.randomUUID()
		dao!!.add(
				AccountMembership(
						user = "TEST",
						groupId = groupId,
						id = UUID.randomUUID()
				)
		)

		assertTrue { dao!!.isAccountMember("TEST", groupId) }
		assertFalse { dao!!.isAccountMember("TEST", UUID.randomUUID()) }
		assertFalse { dao!!.isAccountMember("SOME-OTHER-USER", groupId) }
	}

	@Test
	fun listByUsername() {
		dao!!.add(
				AccountMembership(
						user = "TEST",
						groupId = UUID.randomUUID(),
						id = UUID.randomUUID()
				)
		)

		assertEquals(listOf<AccountMembership>(), dao!!.listByUsername("SOME-OTHER-USER"))
		assertEquals(1, dao!!.listByUsername("TEST").size)

	}

	@Test
	fun listByGroupId() {
		val groupId = UUID.randomUUID()
		dao!!.add(
				AccountMembership(
						user = "TEST",
						groupId = groupId,
						id = UUID.randomUUID()
				)
		)

		assertEquals(listOf<AccountMembership>(), dao!!.listByGroupId(UUID.randomUUID()))
		assertEquals(1, dao!!.listByGroupId(groupId).size)
	}
}