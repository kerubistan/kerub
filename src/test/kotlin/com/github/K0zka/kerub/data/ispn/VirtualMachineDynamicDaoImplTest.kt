package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.mock
import org.infinispan.Cache
import org.infinispan.manager.DefaultCacheManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class VirtualMachineDynamicDaoImplTest {

	val eventListener: EventListener = mock()

	var cacheManager: DefaultCacheManager? = null
	var cache: Cache<UUID, VirtualMachineDynamic>? = null
	var dao: VirtualMachineDynamicDao? = null

	@Before
	fun setup() {
		cacheManager = DefaultCacheManager()
		cacheManager!!.start()
		cache = cacheManager!!.getCache("test")
		cache!!.clear()
		dao = VirtualMachineDynamicDaoImpl(cache!!, eventListener)
	}

	@After fun cleanup() {
		cacheManager?.stop()
	}

	@Test
	fun findByHostId() {

		val host1Id = UUID.randomUUID()
		val host2Id = UUID.randomUUID()

		val dyn1 = VirtualMachineDynamic(
				id = UUID.randomUUID(),
				hostId = host1Id,
				status = VirtualMachineStatus.Up,
				memoryUsed = "1 GB".toSize()
		)

		val dyn2 = VirtualMachineDynamic(
				id = UUID.randomUUID(),
				hostId = host2Id,
				status = VirtualMachineStatus.Up,
				memoryUsed = "1 GB".toSize()
		)

		dao!!.add(dyn1)
		dao!!.add(dyn2)

		assertEquals(dao!!.findByHostId(host1Id), listOf(dyn1))
		assertEquals(dao!!.findByHostId(host2Id), listOf(dyn2))
	}

}