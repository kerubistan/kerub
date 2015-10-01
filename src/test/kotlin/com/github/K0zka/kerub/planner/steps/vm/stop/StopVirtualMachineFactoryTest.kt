package com.github.K0zka.kerub.planner.steps.vm.stop

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.services.impl.GB
import org.junit.Assert
import org.junit.Test
import java.util.*

public class StopVirtualMachineFactoryTest {
	@Test
	fun produceWithBlankState() {
		val state = OperationalState.fromLists()
		val steps = StopVirtualMachineFactory.produce(state)
		Assert.assertTrue(steps.isEmpty())
	}

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
		        memoryUsed = 1.GB(),
		        lastUpdated = System.currentTimeMillis()
		                                 )
		val state = OperationalState.fromLists(vms = listOf(vm), hosts = listOf(host), vmDyns = listOf(vmDyn))
		val steps = StopVirtualMachineFactory.produce(state)
		Assert.assertTrue(steps.isNotEmpty())
	}

}