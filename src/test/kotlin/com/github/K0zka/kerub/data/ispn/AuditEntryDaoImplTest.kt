package com.github.K0zka.kerub.data.ispn

import org.junit.Test
import org.junit.Before
import org.infinispan.AdvancedCache
import java.util.UUID
import com.github.K0zka.kerub.model.AuditEntry
import com.github.K0zka.kerub.data.AuditEntryDao
import org.infinispan.manager.DefaultCacheManager
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import org.mockito.Mock
import com.github.K0zka.kerub.model.Entity
import org.mockito.Mockito
import org.junit.Assert
import org.junit.After

RunWith(javaClass<MockitoJUnitRunner>())
public class AuditEntryDaoImplTest {

	Mock
	var oldEntity : Entity<UUID>? = null
	Mock
	var newEntity : Entity<UUID>? = null

	var cacheManager : DefaultCacheManager? = null
	var cache: AdvancedCache<UUID, AuditEntry>? = null
	var dao: AuditEntryDao? = null

	Before
	fun setup() {
		cacheManager = DefaultCacheManager()
		cacheManager!!.start()
		cache = (cacheManager!!.getCache<UUID, AuditEntry>("test")!! as AdvancedCache<UUID, AuditEntry>)
		dao = AuditEntryDaoImpl(cache!!)
	}

	After
	fun cleanup() {
		cacheManager?.stop()
	}

	Test
	fun add() {
		Mockito.`when`(oldEntity!!.id)!!
				.thenReturn(UUID.fromString("43dcc6e7-cfcd-44af-a4e5-bbe8f7d948cc"))
		Mockito.`when`(newEntity!!.id)!!
				.thenReturn(UUID.fromString("43dcc6e7-cfcd-44af-a4e5-bbe8f7d948cc"))
		dao!!.add(AuditEntry(
				old = oldEntity!!,
				new = newEntity!!,
				user = UUID.randomUUID()))

		val list = dao!!.listById(UUID.fromString("43dcc6e7-cfcd-44af-a4e5-bbe8f7d948cc"))
		Assert.assertEquals(1, list.size)

	}
}