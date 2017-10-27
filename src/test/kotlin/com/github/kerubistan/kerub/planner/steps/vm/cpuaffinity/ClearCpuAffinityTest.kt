package com.github.kerubistan.kerub.planner.steps.vm.cpuaffinity

import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.toSize
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ClearCpuAffinityTest {
	@Test
	fun take() {
		val state = ClearCpuAffinity(vm = testVm, host = testHost).take(OperationalState.fromLists(
				hosts = listOf(testHost),
				hostDyns = listOf(
						HostDynamic(
								id = testHost.id,
								status = HostStatus.Up
						)
				),
				vms = listOf(testVm),
				vmDyns = listOf(VirtualMachineDynamic(
						id = testVm.id,
						status = VirtualMachineStatus.Up,
						hostId = testHost.id,
						memoryUsed = "1 GB".toSize()
				))
		))

		assertNull(requireNotNull(state.vms[testVm.id]?.dynamic).coreAffinity)
		assertEquals(testHost.id, requireNotNull(state.vms[testVm.id]?.dynamic).hostId)
	}

}