package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.testHost
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class HostDaoImplTest :AbstractIspnDaoTest<UUID, Host>() {
	@Test
	fun byAddress() {
		val host1 = testHost.copy(
				id = UUID.randomUUID(),
				address = "test-1.example.com"
		)
		val host2 = testHost.copy(
				id = UUID.randomUUID(),
				address = "test-2.example.com"
		)
		val dao = HostDaoImpl(cache!!, eventListener, auditManager)

		dao.add(host1)
		dao.add(host2)

		assertEquals(listOf<Host>(), dao.byAddress("test"))
		assertEquals(listOf(host1), dao.byAddress(host1.address))
		assertEquals(listOf(host2), dao.byAddress(host2.address))
	}

	@Test
	fun fieldSearch() {
		val host1 = testHost.copy(
				id = UUID.randomUUID(),
				address = "test-1.example.com"
		)
		val host2 = testHost.copy(
				id = UUID.randomUUID(),
				address = "test-2.example.com"
		)
		val dao = HostDaoImpl(cache!!, eventListener, auditManager)

		dao.add(host1)
		dao.add(host2)

		val list = dao.fieldSearch(Host::address.name, "test")
		assertEquals(2, list.size)
		assertTrue(list.contains(host1))
		assertTrue(list.contains(host2))

	}

}