package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.model.AssetOwner
import com.github.kerubistan.kerub.model.AssetOwnerType
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.testVm
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID
import kotlin.test.assertFalse
import kotlin.test.assertNull

class VirtualMachineDaoImplTest : AbstractIspnDaoTest<UUID, VirtualMachine>() {

	@Test
	fun listWithSort() {
		val dao = VirtualMachineDaoImpl(cache!!, eventListener, auditManager)
		val vm1 = testVm.copy(
				name = "test-vm-1",
				id = UUID.randomUUID()
		)
		val vm2 = testVm.copy(
				name = "test-vm-2",
				id = UUID.randomUUID()
		)
		dao.add(vm1)
		dao.add(vm2)
		assertEquals(listOf(vm1, vm2), dao.list(0, Int.MAX_VALUE, "name"))
	}

	@Test
	fun listWithLimit() {
		val dao = VirtualMachineDaoImpl(cache!!, eventListener, auditManager)
		val vm1 = testVm.copy(
				name = "test-vm-1",
				id = UUID.randomUUID()
		)
		val vm2 = testVm.copy(
				name = "test-vm-2",
				id = UUID.randomUUID()
		)
		dao.add(vm1)
		dao.add(vm2)
		assertEquals(listOf(vm1), dao.list(0, 1, "name"))
	}

	@Test
	fun remove() {
		val dao = VirtualMachineDaoImpl(cache!!, eventListener, auditManager)
		val vm1 = testVm.copy(
				name = "test-vm-1",
				id = UUID.randomUUID()
		)
		dao.add(
				vm1
		)
		dao.remove(vm1.id)

		assertNull(dao[vm1.id])
		verify(auditManager).auditDelete(any())
	}

	@Test
	fun fieldSearchWithOwners() {
		val dao = VirtualMachineDaoImpl(cache!!, eventListener, auditManager)
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
		dao.add(
				vm1
		)
		dao.add(
				vm2
		)

		val listBoth = dao.fieldSearch(setOf(owner1, owner2), "name", "test-vm", 0, Int.MAX_VALUE)
		assertEquals(2, listBoth.size)
		assertTrue(listBoth.contains(vm1))
		assertTrue(listBoth.contains(vm2))

		val listOne = dao.fieldSearch(setOf(owner1), "name", "test-vm", 0, Int.MAX_VALUE)
		assertEquals(1, listOne.size)
		assertTrue(listOne.contains(vm1))
		assertFalse(listOne.contains(vm2))

		assertTrue(dao.fieldSearch(setOf(), "name", "test-vm", 0, Int.MAX_VALUE).isEmpty())
	}

	@Test
	fun fieldSearch() {
		val dao = VirtualMachineDaoImpl(cache!!, eventListener, auditManager)
		val vm = testVm.copy(
				name = "test-vm-1",
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = UUID.randomUUID(),
						ownerType = AssetOwnerType.account
				)
		)
		dao.add(
				vm
		)
		assertTrue(dao.fieldSearch(field = "name", start = 0, limit = Int.MAX_VALUE, value = "test").contains(vm))
		assertTrue(dao.fieldSearch(field = "name", start = 0, limit = Int.MAX_VALUE, value = "test-vm").contains(vm))
		assertTrue(dao.fieldSearch(field = "name", start = 0, limit = Int.MAX_VALUE, value = "test-vm-1").contains(vm))
	}

	@Test
	fun listByOwners() {
		val dao = VirtualMachineDaoImpl(cache!!, eventListener, auditManager)
		val vm = testVm.copy(
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = UUID.randomUUID(),
						ownerType = AssetOwnerType.account
				)
		)
		dao.add(
				vm
		)
		assertEquals(listOf<VirtualMachine>(), dao.listByOwners(setOf()))
		assertEquals(listOf(vm), dao.listByOwners(setOf(vm.owner!!)))
		assertEquals(listOf<VirtualMachine>(), dao.listByOwners(setOf(
				AssetOwner(ownerId = UUID.randomUUID(), ownerType = AssetOwnerType.account)
		)))

	}

	@Test
	fun listByOwner() {
		val dao = VirtualMachineDaoImpl(cache!!, eventListener, auditManager)
		val vm = testVm.copy(
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = UUID.randomUUID(),
						ownerType = AssetOwnerType.account
				)
		)
		dao.add(
				vm
		)
		assertEquals(listOf(vm), dao.listByOwner(vm.owner!!))
	}

	@Test
	fun countWithOwner() {
		val dao = VirtualMachineDaoImpl(cache!!, eventListener, auditManager)
		val vm1 = testVm.copy(
				name = "vm-1",
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = UUID.randomUUID(),
						ownerType = AssetOwnerType.account
				)
		)
		dao.add(
				vm1
		)

		assertEquals(1, dao.count(setOf(vm1.owner!!)))

		val vm2 = testVm.copy(
				name = "vm-2",
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = UUID.randomUUID(),
						ownerType = AssetOwnerType.account
				)
		)
		dao.add(
				vm2
		)
		assertEquals("Since vm2 has different owner, should still be just 1", 1, dao.count(setOf(vm1.owner!!)))

		val vm3 = testVm.copy(
				name = "vm-3",
				id = UUID.randomUUID(),
				owner = vm1.owner
		)
		dao.add(
				vm3
		)
		assertEquals("Since vm3 has same owner as vm1, should be 2", 2, dao.count(setOf(vm1.owner!!)))
		assertEquals("Since vm3 has same owner as vm1, should be 2", 0, dao.count(setOf()))
	}

	@Test
	fun getByIds() {
		val dao = VirtualMachineDaoImpl(cache!!, eventListener, auditManager)
		val vm1 = testVm.copy(
				name = "vm-1",
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = UUID.randomUUID(),
						ownerType = AssetOwnerType.account
				)
		)
		dao.add(
				vm1
		)

		val vm2 = testVm.copy(
				name = "vm-2",
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = UUID.randomUUID(),
						ownerType = AssetOwnerType.account
				)
		)
		dao.add(
				vm2
		)

		assertEquals(listOf(vm1), dao.get(listOf(vm1.id)))
		assertEquals(listOf<VirtualMachine>(), dao.get(listOf()))

		val both = dao.get(listOf(vm1.id, vm2.id))
		assertEquals(2, both.size)
		assertTrue(both.contains(vm1))
		assertTrue(both.contains(vm2))
	}

	@Test
	fun listByAttachedStorage() {
		val dao = VirtualMachineDaoImpl(cache!!, eventListener, auditManager)
		val storageId = UUID.randomUUID()
		val vm1 = testVm.copy(
				name = "vm-1",
				id = UUID.randomUUID(),
				virtualStorageLinks = listOf(
						VirtualStorageLink(
								virtualStorageId = storageId,
								bus = BusType.sata,
								device = DeviceType.cdrom,
								readOnly = true
						)
				)
		)
		dao.add(
				vm1
		)

		assertEquals(listOf(vm1), dao.listByAttachedStorage(storageId))
		assertEquals(listOf<VirtualMachine>(), dao.listByAttachedStorage(UUID.randomUUID()))
	}

	@Test
	fun getByName() {
		val dao = VirtualMachineDaoImpl(cache!!, eventListener, auditManager)

		val vm1 = testVm.copy(
				name = "vm-1",
				id = UUID.randomUUID()
		)

		val vm2 = testVm.copy(
				name = "vm-2",
				id = UUID.randomUUID()
		)

		dao.add(vm1)
		dao.add(vm2)

		assertEquals(listOf(vm1), dao.getByName(vm1.name))
		assertEquals(listOf(vm2), dao.getByName(vm2.name))
		assertEquals(listOf<VirtualMachine>(), dao.getByName("NOTEXISTING-${UUID.randomUUID()}"))
	}

	@Test()
	fun existsByName() {
		val dao = VirtualMachineDaoImpl(cache!!, eventListener, auditManager)

		val vm1 = testVm.copy(
				name = "vm-1",
				id = UUID.randomUUID()
		)

		val vm2 = testVm.copy(
				name = "vm-2",
				id = UUID.randomUUID()
		)

		dao.add(vm1)
		dao.add(vm2)

		assertTrue(dao.existsByName(vm1.name))
		assertTrue(dao.existsByName(vm2.name))
		assertFalse { dao.existsByName("not-existing-vm-${System.currentTimeMillis()}") }
	}

	@Test
	fun getByNameWithOwner() {
		val dao = VirtualMachineDaoImpl(cache!!, eventListener, auditManager)

		val owner1 = AssetOwner(
				ownerId = UUID.randomUUID(),
				ownerType = AssetOwnerType.account
		)
		val owner2 = AssetOwner(
				ownerId = UUID.randomUUID(),
				ownerType = AssetOwnerType.account
		)
		val owner3 = AssetOwner(
				ownerId = UUID.randomUUID(),
				ownerType = AssetOwnerType.account
		)

		val vm1 = testVm.copy(
				name = "test-vm",
				id = UUID.randomUUID(),
				owner = owner1
		)

		val vm2 = vm1.copy(
				id = UUID.randomUUID(),
				owner = owner2
		)

		dao.add(vm1)
		dao.add(vm2)

		assertEquals(listOf(vm1), dao.getByName(owner1, vm1.name))
		assertEquals(listOf(vm2), dao.getByName(owner2, vm1.name))
		assertEquals(listOf<VirtualMachine>(), dao.getByName(owner3, vm1.name))
	}

}