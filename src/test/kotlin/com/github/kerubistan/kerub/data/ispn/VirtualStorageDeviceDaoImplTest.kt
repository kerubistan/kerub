package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.testDisk
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class VirtualStorageDeviceDaoImplTest :AbstractIspnDaoTest<UUID, VirtualStorageDevice>() {

	@Test
	fun list() {
		VirtualStorageDeviceDaoImpl(cache!!, mock(), mock()).apply {
			val disk1 = testDisk.copy(
					id = UUID.randomUUID(),
					name = "test-disk-1"
			)
			val disk2 = testDisk.copy(
					id = UUID.randomUUID(),
					name = "test-disk-1"
			)
			val disk3 = testDisk.copy(
					id = UUID.randomUUID(),
					name = "test-disk-1"
			)
			add(disk1)
			add(disk2)
			add(disk3)

			val result = list(listOf(disk1.id, disk2.id))

			assertEquals(2, result.size)
			assertTrue(result.contains(disk1))
			assertTrue(result.contains(disk2))
			assertFalse(result.contains(disk3))
		}
	}

	@Test
	fun addAll() {
		VirtualStorageDeviceDaoImpl(cache!!, mock(), mock()).apply {
			val disk1 = testDisk.copy(
					id = UUID.randomUUID(),
					name = "test-disk-1"
			)
			val disk2 = testDisk.copy(
					id = UUID.randomUUID(),
					name = "test-disk-1"
			)
			val disk3 = testDisk.copy(
					id = UUID.randomUUID(),
					name = "test-disk-1"
			)
			addAll(listOf(disk1, disk2, disk3))

			val result = list()

			assertEquals(3, result.size)
			assertTrue(result.contains(disk1))
			assertTrue(result.contains(disk2))
			assertTrue(result.contains(disk3))
		}
	}

	@Test
	fun getEmptyList() {
		VirtualStorageDeviceDaoImpl(cache!!, mock(), mock()).apply {
			assertEquals(listOf<VirtualStorageDevice>(), list(listOf()))
		}
	}

}