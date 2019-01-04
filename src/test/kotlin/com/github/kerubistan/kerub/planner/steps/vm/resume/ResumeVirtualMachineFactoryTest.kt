package com.github.kerubistan.kerub.planner.steps.vm.resume

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import org.junit.Test
import java.math.BigInteger
import java.util.UUID
import kotlin.test.assertEquals

class ResumeVirtualMachineFactoryTest : AbstractFactoryVerifications(ResumeVirtualMachineFactory) {

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
							status = VirtualMachineStatus.Paused,
							hostId = host.id,
							memoryUsed = BigInteger("1234567")
					)
			),
			hosts = listOf(host)
	)

	@Test
	fun produce() {
		val steps = ResumeVirtualMachineFactory.produce(state)
		assertEquals(1, steps.size)
		assertEquals(vm, steps[0].vm)
		assertEquals(host, steps[0].host)
	}
}