package com.github.kerubistan.kerub.planner.steps.vm.stop

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import io.github.kerubistan.kroki.time.now
import org.junit.Assert
import org.junit.Test
import java.util.UUID

class StopVirtualMachineFactoryTest : AbstractFactoryVerifications(StopVirtualMachineFactory) {

	@Test
	fun produceWithSingleRunningVm() {
		val vm = VirtualMachine(
				name = "test-vm",
				id = UUID.randomUUID(),
				nrOfCpus = 1
		)
		val host = Host(
				id = UUID.randomUUID(),
				address = "host-1.example.com",
				dedicated = true,
				publicKey = "test"
		)
		val vmDyn = VirtualMachineDynamic(
				id = vm.id,
				hostId = host.id,
				status = VirtualMachineStatus.Up,
				memoryUsed = 1.GB,
				lastUpdated = now()
		)
		val state = OperationalState.fromLists(vms = listOf(vm), hosts = listOf(host), vmDyns = listOf(vmDyn))
		val steps = StopVirtualMachineFactory.produce(state)
		Assert.assertTrue(steps.isNotEmpty())
	}

}