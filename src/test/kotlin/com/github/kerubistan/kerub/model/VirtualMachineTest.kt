package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.model.expectations.NotSameHostExpectation
import com.github.kerubistan.kerub.model.expectations.VmDependency
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testVm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class VirtualMachineTest {

	@Test
	fun validations() {
		assertThrows<IllegalStateException> {
			testVm.copy(memory = Range(1.toBigInteger(), (-2).toBigInteger()))
		}
		assertThrows<IllegalStateException> {
			testVm.copy(memory = Range((-1).toBigInteger(), (2).toBigInteger()))
		}
	}

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
		val vmRefs = refs.getValue(VirtualMachine::class)
		assertEquals(2, vmRefs.size)
		assertTrue(vmRefs.contains(otherVmId))
		assertTrue(vmRefs.contains(thirdVmId))
		val vStorageRefs = refs.getValue(VirtualStorageDevice::class)
		assertEquals(vStorageRefs, listOf(testDisk.id))
	}
}