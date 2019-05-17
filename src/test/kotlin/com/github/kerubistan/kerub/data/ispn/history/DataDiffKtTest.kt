package com.github.kerubistan.kerub.data.ispn.history

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.CpuStat
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.toSize
import io.github.kerubistan.kroki.time.now
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class DataDiffKtTest {

	data class Person(
			val name : String,
			val address : String? = null
	)

	@Test
	fun diffNulls() {
		assertNotNull(diff(Person("bob", null) , Person("bob", "Somewhere")))
		assertNotNull(diff(Person("bob", "Somewhere") , Person("bob", null)))
	}

	@Test
	fun diffVmDyns() {
		val diff = diff(
				VirtualMachineDynamic(
						id = testVm.id,
						hostId = testHost.id,
						status = VirtualMachineStatus.Up,
						lastUpdated = now() - 100,
						memoryUsed = 1.GB,
						cpuUsage = listOf(
								CpuStat(
										cpuNr = 0,
										idle = 0f,
										ioWait = 0f,
										system = 0f,
										user = 0f
								),
								CpuStat(
										cpuNr = 1,
										idle = 0f,
										ioWait = 0f,
										system = 0f,
										user = 0f
								)
						)
				),
				VirtualMachineDynamic(
						id = testVm.id,
						hostId = testHost.id,
						status = VirtualMachineStatus.Up,
						lastUpdated = now(),
						memoryUsed = "1.1 GB".toSize(),
						cpuUsage = listOf(
								CpuStat(
										cpuNr = 0,
										idle = 0f,
										ioWait = 0f,
										system = 0f,
										user = 0f
								),
								CpuStat(
										cpuNr = 1,
										idle = 0f,
										ioWait = 4f,
										system = 5f,
										user = 0f
								)
						)
				)
		)
		assertFalse { diff.isEmpty() }
	}
}