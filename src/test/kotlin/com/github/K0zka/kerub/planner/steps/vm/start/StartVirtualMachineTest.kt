package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.planner.OperationalState
import org.junit.Assert
import org.junit.Test

class StartVirtualMachineTest {
	@Test
	fun take() {
		val host = Host(
				address = "host-1.example.com",
				dedicated = true,
				publicKey = "test"
		               )
		val vm = VirtualMachine(
				name = "text-vm"
		                       )
		val originalState = OperationalState.fromLists(
				vms = listOf(vm),
				hosts = listOf(host)
		                                              )

		val transformed = StartVirtualMachine(vm = vm, host = host).take(originalState)
		Assert.assertTrue(transformed.vms.containsKey(vm.id))
		Assert.assertTrue(transformed.hosts.containsKey(host.id))
		Assert.assertTrue(transformed.hostDyns.containsKey(host.id))
		Assert.assertTrue(transformed.vmDyns.containsKey(vm.id))
		Assert.assertTrue(transformed.vmDyns[vm.id]!!.status == VirtualMachineStatus.Up)
		Assert.assertTrue(transformed.vmDyns[vm.id]!!.hostId == host.id)
	}
}