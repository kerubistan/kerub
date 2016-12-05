package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.model.AssetOwner
import com.github.K0zka.kerub.model.AssetOwnerType
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.testVm
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.infinispan.Cache
import org.infinispan.manager.DefaultCacheManager
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertFalse
import kotlin.test.assertNull

class VirtualMachineDaoImplTest {

	val eventListener: EventListener = mock()
	val auditManager: AuditManager = mock()

	var cacheManager: DefaultCacheManager? = null
	var cache: Cache<UUID, VirtualMachine>? = null
	var dao: VirtualMachineDao? = null

	@Before
	fun setup() {
		cacheManager = DefaultCacheManager()
		cacheManager!!.start()
		cache = cacheManager!!.getCache("test")
		cache!!.clear()
		dao = VirtualMachineDaoImpl(cache!!, eventListener, auditManager)
	}

	@After fun cleanup() {
		cacheManager?.stop()
	}

	@Test
	fun remove() {
		val vm1 = testVm.copy(
				name = "test-vm-1",
				id = UUID.randomUUID()
		)
		dao!!.add(
				vm1
		)
		dao!!.remove(vm1.id)

		assertNull(dao!![vm1.id])
		verify(auditManager).auditDelete(any())
	}

	@Test
	fun fieldSearchWithOwners() {
		val owner1 = AssetOwner(
				ownerId = UUID.randomUUID(),
				ownerType = AssetOwnerType.account
		)
		val owner2 = AssetOwner(
				ownerId = UUID.randomUUID(),
				ownerType = AssetOwnerType.account
		)
		val vm1 = testVm.copy(
				name = "test-vm-1",
				id = UUID.randomUUID(),
				owner = owner1
		)
		val vm2 = testVm.copy(
				name = "test-vm-2",
				id = UUID.randomUUID(),
				owner = owner2
		)
		dao!!.add(
				vm1
		)
		dao!!.add(
				vm2
		)

		val listBoth = dao!!.fieldSearch(setOf(owner1, owner2), "name", "test-vm", 0, Int.MAX_VALUE)
		assertEquals(2, listBoth.size)
		assertTrue(listBoth.contains(vm1))
		assertTrue(listBoth.contains(vm2))

		val listOne = dao!!.fieldSearch(setOf(owner1), "name", "test-vm", 0, Int.MAX_VALUE)
		assertEquals(1, listOne.size)
		assertTrue(listOne.contains(vm1))
		assertFalse(listOne.contains(vm2))

		assertTrue(dao!!.fieldSearch(setOf(), "name", "test-vm", 0, Int.MAX_VALUE).isEmpty())
	}

	@Test
	fun fieldSearch() {
		val vm = testVm.copy(
				name = "test-vm-1",
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = UUID.randomUUID(),
						ownerType = AssetOwnerType.account
				)
		)
		dao!!.add(
				vm
		)
		assertTrue(dao!!.fieldSearch(field = "name", start = 0, limit = Int.MAX_VALUE, value = "test").contains(vm))
		assertTrue(dao!!.fieldSearch(field = "name", start = 0, limit = Int.MAX_VALUE, value = "test-vm").contains(vm))
		assertTrue(dao!!.fieldSearch(field = "name", start = 0, limit = Int.MAX_VALUE, value = "test-vm-1").contains(vm))
	}

	@Test
	fun listByOwners() {
		val vm = testVm.copy(
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = UUID.randomUUID(),
						ownerType = AssetOwnerType.account
				)
		)
		dao!!.add(
				vm
		)
		assertEquals(listOf(vm), dao!!.listByOwners(setOf(vm.owner!!)))
		assertEquals(listOf<VirtualMachine>(), dao!!.listByOwners(setOf(
				AssetOwner(ownerId = UUID.randomUUID(), ownerType = AssetOwnerType.account)
		)))

	}

	@Test
	fun listByOwner() {
		val vm = testVm.copy(
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = UUID.randomUUID(),
						ownerType = AssetOwnerType.account
				)
		)
		dao!!.add(
				vm
		)
		val list = dao!!.listByOwner(vm.owner!!)

		assertEquals(listOf(vm), list)
	}

	@Test
	fun countWithOwner() {
		val vm1 = testVm.copy(
				name = "vm-1",
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = UUID.randomUUID(),
						ownerType = AssetOwnerType.account
				)
		)
		dao!!.add(
				vm1
		)

		assertEquals(1, dao!!.count(setOf(vm1.owner!!)))

		val vm2 = testVm.copy(
				name = "vm-2",
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = UUID.randomUUID(),
						ownerType = AssetOwnerType.account
				)
		)
		dao!!.add(
				vm2
		)
		assertEquals("Since vm2 has different owner, should still be just 1", 1, dao!!.count(setOf(vm1.owner!!)))

		val vm3 = testVm.copy(
				name = "vm-3",
				id = UUID.randomUUID(),
				owner = vm1.owner
		)
		dao!!.add(
				vm3
		)
		assertEquals("Since vm3 has same owner as vm1, should be 2", 2, dao!!.count(setOf(vm1.owner!!)))
	}
}