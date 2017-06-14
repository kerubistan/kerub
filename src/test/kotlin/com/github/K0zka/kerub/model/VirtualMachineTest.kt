package com.github.K0zka.kerub.model

import com.github.K0zka.kerub.model.expectations.NotSameHostExpectation
import com.github.K0zka.kerub.model.expectations.VmDependency
import com.github.K0zka.kerub.model.io.BusType
import com.github.K0zka.kerub.model.io.DeviceType
import com.github.K0zka.kerub.testDisk
import com.github.K0zka.kerub.testVm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class VirtualMachineTest {

	@Test
	fun references() {
		val otherVmId = UUID.randomUUID()
		val thirdVmId = UUID.randomUUID()
		val refs = testVm.copy(
				expectations = listOf(
						NotSameHostExpectation(
								otherVmId = otherVmId
						),
						VmDependency(
								otherVm = thirdVmId
						)
				),
				virtualStorageLinks = listOf(
						VirtualStorageLink(
								virtualStorageId = testDisk.id,
								bus = BusType.sata,
								device = DeviceType.disk
						)
				)
		).references()

		assertEquals(2, refs.keys.size)
		val vmRefs = refs[VirtualMachine::class]!!
		assertEquals(2, vmRefs.size)
		assertTrue(vmRefs.contains(otherVmId))
		assertTrue(vmRefs.contains(thirdVmId))
		val vStorageRefs = refs[VirtualStorageDevice::class]!!
		assertEquals(vStorageRefs, listOf(testDisk.id))
	}
}