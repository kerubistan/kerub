package com.github.kerubistan.kerub.data.ispn.history

import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamicItem
import com.github.kerubistan.kerub.model.dynamic.CpuStat
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testLvmCapability
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.toSize
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import io.github.kerubistan.kroki.time.now
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DataDiffKtTest {

	data class Person(
			val name: String,
			val address: String? = null
	)

	@Test
	fun diffNulls() {
		assertNotNull(diff(Person("bob", null), Person("bob", "Somewhere")))
		assertNotNull(diff(Person("bob", "Somewhere"), Person("bob", null)))
	}

	@Test
	fun diffHostDyns() {
		val diff = diff(
				oldData = HostDynamic(
						id = testHost.id,
						storageStatus = listOf(
								CompositeStorageDeviceDynamic(
										id = testLvmCapability.id,
										reportedFreeCapacity = 1.TB,
										items = listOf(
												CompositeStorageDeviceDynamicItem(
														name = "/dev/sda", freeCapacity = 512.GB
												),
												CompositeStorageDeviceDynamicItem(
														name = "/dev/sdb", freeCapacity = 512.GB
												)
										)
								)
						)
				),
				newData = HostDynamic(
						id = testHost.id,
						storageStatus = listOf(
								CompositeStorageDeviceDynamic(
										id = testLvmCapability.id,
										reportedFreeCapacity = 600.GB,
										items = listOf(
												CompositeStorageDeviceDynamicItem(
														name = "/dev/sda", freeCapacity = 512.GB
												),
												CompositeStorageDeviceDynamicItem(
														name = "/dev/sdb", freeCapacity = 88.GB
												)
										)
								)
						)
				)
		)

		assertTrue("Storage status by id is should not be in the diff " +
				"as it is computed and therefore have @JsonIgnore") {
			diff.none { it.property == HostDynamic::storageStatusById.name }
		}
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