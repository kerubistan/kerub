package com.github.kerubistan.kerub.planner.steps

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.host.powerdown.PowerDownHost
import com.github.kerubistan.kerub.planner.steps.vm.start.kvm.KvmStartVirtualMachine
import org.junit.Assert
import org.junit.Test

class StepBenefitComparatorTest {

	val virtualMachine = VirtualMachine(
			name = "vm1",
			expectations = listOf(
					VirtualMachineAvailabilityExpectation(
							up = true
					                                     )
			                     )
	                                   )

	val host = Host(
			address = "host-1.example.com",
	        publicKey = "",
	        dedicated = true
	               )

	val state = OperationalState.fromLists(
			vms = listOf(virtualMachine),
	        hosts = listOf(host),
	        hostDyns = listOf(HostDynamic(
			        id = host.id,
	                status = HostStatus.Up
	                               ))
	                            )

	val start = KvmStartVirtualMachine(
			vm = virtualMachine,
	        host = host
	                               )

	val stopHost = PowerDownHost(
			host = host
	                            )

	@Test
	fun compare() {
		Assert.assertEquals(StepBenefitComparator(state).compare(start, start), 0)
		Assert.assertEquals(StepBenefitComparator(state).compare(stopHost, stopHost), 0)
		Assert.assertTrue(StepBenefitComparator(state).compare(start, stopHost) < 0)
		Assert.assertTrue(StepBenefitComparator(state).compare(stopHost, start) > 0)
	}
}