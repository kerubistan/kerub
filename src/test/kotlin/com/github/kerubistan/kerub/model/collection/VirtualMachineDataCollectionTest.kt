package com.github.kerubistan.kerub.model.collection

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class VirtualMachineDataCollectionTest {
	@Test
	fun validation() {
		assertThrows<IllegalStateException>("stat and dyn id must be the same") {
			VirtualMachineDataCollection(
					stat = testVm,
					dynamic = VirtualMachineDynamic(
							id = UUID.randomUUID(),
							hostId = testHost.id,
							status = VirtualMachineStatus.Up,
							memoryUsed = 1.GB
					)
			)

		}
	}
}