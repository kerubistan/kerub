package com.github.K0zka.kerub.planner.steps.vm.cpuaffinity

import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.testHost
import com.github.K0zka.kerub.testVm
import com.github.K0zka.kerub.utils.toSize
import org.junit.Test
import kotlin.test.assertEquals

class SetCpuAffinityTest {
	@Test
	fun take() {
		val state = SetCpuAffinity(vm = testVm, cpus = listOf(1, 2), host = testHost).take(OperationalState.fromLists(
				hosts = listOf(testHost),
				hostDyns = listOf(HostDynamic(
						id = testHost.id,
						status = HostStatus.Up
				)),
				vms = listOf(testVm),
				vmDyns = listOf(VirtualMachineDynamic(
						id = testVm.id,
						status = VirtualMachineStatus.Up,
						memoryUsed = "1 GB".toSize(),
						hostId = testHost.id
				))
		))
		assertEquals(listOf(1, 2), state.vms[testVm.id]?.dynamic?.coreAffinity)
		assertEquals(testHost.id, state.vms[testVm.id]?.dynamic?.hostId, "cpu pinning should not change host id")
	}

}