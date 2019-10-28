package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.vmUp
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class VirtualMachineDynamicTest : AbstractDataRepresentationTest<VirtualMachineDynamic>(){
	override val testInstances = listOf(
			vmUp(testVm, testHost)
	)
	override val clazz = VirtualMachineDynamic::class.java

	@Test
	fun validations() {
		assertThrows<IllegalStateException>("Illegal value for memory used") {
			VirtualMachineDynamic(
					id = testVm.id,
					hostId = testHost.id,
					status = VirtualMachineStatus.Up,
					memoryUsed = (-1).toBigInteger()
			)
		}
	}
}