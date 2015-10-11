package com.github.K0zka.kerub.planner.steps.vm.stop

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.Risk
import com.github.K0zka.kerub.utils.toSize
import org.junit.Assert
import org.junit.Test

public class StopVirtualMachineTest {
	@Test
	fun take() {
		val host = Host(
				address = "host-1.example.com",
				dedicated = true,
				publicKey = "test"
		               )
		val hostDyn = HostDynamic(
				id = host.id,
				lastUpdated = System.currentTimeMillis(),
				status = HostStatus.Up
		                         )
		val vm = VirtualMachine(
				name = "text-vm"
		                       )
		val vmDyn = VirtualMachineDynamic(
				id = vm.id,
				hostId = host.id,
				status = VirtualMachineStatus.Up,
				memoryUsed = "1 GB".toSize(),
				lastUpdated = System.currentTimeMillis()
		                                 )
		val originalState = OperationalState.fromLists(
				vms = listOf(vm),
				hosts = listOf(host),
				vmDyns = listOf(vmDyn),
		        hostDyns = listOf(hostDyn)
		                                              )

		val transformed = StopVirtualMachine(vm = vm, host = host).take(originalState)

		Assert.assertTrue(transformed.vms.containsKey(vm.id))
		Assert.assertFalse(transformed.vmDyns.containsKey(vm.id))
	}

	@Test
	fun getCostsWithoutAvailablityExpectation() {
		val host = Host(
				address = "host-1.example.com",
				dedicated = true,
				publicKey = "test"
		               )
		val vm = VirtualMachine(
				name = "text-vm"
		                       )
		val costs = StopVirtualMachine(vm = vm, host = host).getCost(OperationalState.fromLists())
		Assert.assertTrue(costs.isEmpty())
	}

	@Test
	fun getCostsWithAvailablityExpectation() {
		val host = Host(
				address = "host-1.example.com",
				dedicated = true,
				publicKey = "test"
		               )
		val vm = VirtualMachine(
				name = "text-vm",
		        expectations = listOf(VirtualMachineAvailabilityExpectation())
		                       )
		val costs = StopVirtualMachine(vm = vm, host = host).getCost(OperationalState.fromLists())
		Assert.assertTrue(costs.isNotEmpty())
		Assert.assertTrue(costs.any {it is Risk} )
	}
}