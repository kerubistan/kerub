package com.github.kerubistan.kerub.planner.steps.vm.pause

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigInteger
import java.util.UUID

class PauseVirtualMachineFactoryTest : AbstractFactoryVerifications(PauseVirtualMachineFactory) {

	val vm = VirtualMachine(
			id = UUID.randomUUID(),
			name = "vm-1"
	)

	val host = Host(
			id = UUID.randomUUID(),
			address = "host-1",
			publicKey = "",
			dedicated = true
	)

	val state = OperationalState.fromLists(
			vms = listOf(vm),
			vmDyns = listOf(
					VirtualMachineDynamic(
							id = vm.id,
							status = VirtualMachineStatus.Up,
							hostId = host.id,
							memoryUsed = BigInteger("1234567")
					)
			),
			hosts = listOf(host)
	)

	@Test
	fun produce() {
		val steps = PauseVirtualMachineFactory.produce(state)
		val step = steps[0]
		assertEquals(vm, step.vm)
		assertEquals(host, step.host)
	}
}