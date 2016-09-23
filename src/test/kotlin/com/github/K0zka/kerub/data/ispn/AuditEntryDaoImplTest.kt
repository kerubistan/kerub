package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.AuditEntryDao
import com.github.K0zka.kerub.model.AddEntry
import com.github.K0zka.kerub.model.AuditEntry
import com.github.K0zka.kerub.model.DeleteEntry
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.model.UpdateEntry
import com.github.K0zka.kerub.utils.toUUID
import com.nhaarman.mockito_kotlin.mock
import org.infinispan.AdvancedCache
import org.infinispan.manager.DefaultCacheManager
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.UUID

class AuditEntryDaoImplTest {

	val oldEntity: Entity<UUID> = mock()
	val newEntity: Entity<UUID> = mock()

	var cacheManager: DefaultCacheManager? = null
	var cache: AdvancedCache<UUID, AuditEntry>? = null
	var dao: AuditEntryDao? = null

	@Before
	fun setup() {
		cacheManager = DefaultCacheManager()
		cacheManager!!.start()
		cache = (cacheManager!!.getCache<UUID, AuditEntry>("test")!! as AdvancedCache<UUID, AuditEntry>)
		dao = AuditEntryDaoImpl(cache!!)
	}

	@After
	fun cleanup() {
		cacheManager?.stop()
	}

	@Test
	fun add() {

		Mockito.`when`(oldEntity.id)!!
				.thenReturn("43dcc6e7-cfcd-44af-a4e5-bbe8f7d948cc".toUUID())
		Mockito.`when`(newEntity.id)!!
				.thenReturn("43dcc6e7-cfcd-44af-a4e5-bbe8f7d948cc".toUUID())

		dao!!.add(AddEntry(
				new = oldEntity,
				user = UUID.randomUUID().toString()))

		dao!!.add(UpdateEntry(
				old = oldEntity,
				new = newEntity,
				user = UUID.randomUUID().toString()))

		dao!!.add(DeleteEntry(
				old = newEntity,
				user = UUID.randomUUID().toString()))

		val list = dao!!.listById("43dcc6e7-cfcd-44af-a4e5-bbe8f7d948cc".toUUID())
		Assert.assertEquals(3, list.size)

	}
}