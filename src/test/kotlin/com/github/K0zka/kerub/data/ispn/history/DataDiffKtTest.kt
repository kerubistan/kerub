package com.github.K0zka.kerub.data.ispn.history

import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.CpuStat
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.testHost
import com.github.K0zka.kerub.testVm
import com.github.K0zka.kerub.utils.toSize
import org.junit.Test
import kotlin.test.assertFalse

class DataDiffKtTest {

	@Test
	fun diffVmDyns() {
		val diff = diff(
				VirtualMachineDynamic(
						id = testVm.id,
						hostId = testHost.id,
						status = VirtualMachineStatus.Up,
						lastUpdated = System.currentTimeMillis() - 100,
						memoryUsed = "1 GB".toSize(),
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
						lastUpdated = System.currentTimeMillis(),
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