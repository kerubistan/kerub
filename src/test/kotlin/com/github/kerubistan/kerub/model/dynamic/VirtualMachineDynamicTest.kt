package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class VirtualMachineDynamicTest {
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