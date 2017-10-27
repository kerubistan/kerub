package com.github.kerubistan.kerub.planner.steps.vm.pause

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigInteger
import java.util.UUID

class PauseVirtualMachineTest {

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
	fun take() {
		val transformed = PauseVirtualMachine(vm, host).take(state)
		assertEquals(VirtualMachineStatus.Paused, transformed.vms[vm.id]?.dynamic!!.status)
	}
}