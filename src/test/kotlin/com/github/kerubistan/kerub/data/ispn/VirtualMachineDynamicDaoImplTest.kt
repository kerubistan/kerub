package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.data.HistoryDao
import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.nhaarman.mockito_kotlin.mock
import io.github.kerubistan.kroki.size.GB
import org.infinispan.Cache
import org.infinispan.manager.DefaultCacheManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class VirtualMachineDynamicDaoImplTest {

	private val eventListener: EventListener = mock()

	private var cacheManager: DefaultCacheManager? = null
	private var cache: Cache<UUID, VirtualMachineDynamic>? = null
	private var historyDao = mock<HistoryDao<VirtualMachineDynamic>>()
	private var dao: VirtualMachineDynamicDao? = null

	@Before
	fun setup() {
		cacheManager = DefaultCacheManager()
		cacheManager!!.start()
		cache = cacheManager!!.getCache("test")
		cache!!.clear()
		dao = VirtualMachineDynamicDaoImpl(cache!!, historyDao, eventListener)
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
				memoryUsed = 1.GB
		)

		val dyn2 = VirtualMachineDynamic(
				id = UUID.randomUUID(),
				hostId = host2Id,
				status = VirtualMachineStatus.Up,
				memoryUsed = 1.GB
		)

		dao!!.add(dyn1)
		dao!!.add(dyn2)

		assertEquals(dao!!.findByHostId(host1Id), listOf(dyn1))
		assertEquals(dao!!.findByHostId(host2Id), listOf(dyn2))
	}

}