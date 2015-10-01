package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.services.impl.GB
import org.junit.Assert
import org.junit.Test
import java.util.UUID

public class StartVirtualMachineFactoryTest {

	val vm = VirtualMachine(
			name = "test-vm"
	                       )
	val host = Host(
			address = "host-1.example.com",
			dedicated = true,
			id = UUID.randomUUID(),
			publicKey = ""
	               )

	val hostDyn = HostDynamic(
			id = host.id,
			status = HostStatus.Up
	                         )


	val vmDyn = VirtualMachineDynamic(
			id = vm.id,
			status = VirtualMachineStatus.Up,
			hostId = host.id,
			memoryUsed = 1.GB()
	                                 )

	@Test
	fun produceWithoutBlankState() {
		val steps = StartVirtualMachineFactory.produce(OperationalState.fromLists())
		Assert.assertTrue(steps.isEmpty())
	}

	@Test
	fun produceWithoutHost() {
		val steps = StartVirtualMachineFactory.produce(OperationalState.fromLists())
		Assert.assertTrue(steps.isEmpty())
	}

	@Test
	fun produceWithoutRunningHosts() {
		val steps = StartVirtualMachineFactory.produce(
				OperationalState.fromLists(
						vms = listOf(vm),
						hosts = listOf(host),
						vmDyns = listOf()))
		Assert.assertTrue(steps.isEmpty())
	}

	@Test
	fun produceWithVmRunning() {
		val steps = StartVirtualMachineFactory.produce(
				OperationalState.fromLists(
						vms = listOf(vm.copy(
								expectations = listOf(VirtualMachineAvailabilityExpectation(
										up = true,
								        level = ExpectationLevel.DealBreaker
								                                                    )
						                    ))),
						hosts = listOf(host),
						vmDyns = listOf(vmDyn),
						hostDyns = listOf(hostDyn)))
		Assert.assertTrue(steps.isEmpty())
	}

	@Test
	fun produceWithVmNotRunning() {
		val vmToRun = vm.copy(
				expectations = listOf(VirtualMachineAvailabilityExpectation(
						up = true,
						level = ExpectationLevel.DealBreaker
				                                                           )
				                     ))
		val steps = StartVirtualMachineFactory.produce(
				OperationalState.fromLists(
						vms = listOf(vmToRun),
						hosts = listOf(host),
						vmDyns = listOf(),
						hostDyns = listOf(hostDyn)))
		Assert.assertTrue(steps.isNotEmpty())
		Assert.assertTrue(steps.any { it is StartVirtualMachine
				&& it.vm == vmToRun
				&& it.host == host } )
	}

}