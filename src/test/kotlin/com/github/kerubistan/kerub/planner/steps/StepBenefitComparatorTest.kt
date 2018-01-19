package com.github.kerubistan.kerub.planner.steps

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.PlanViolationDetectorImpl
import com.github.kerubistan.kerub.planner.steps.host.powerdown.PowerDownHost
import com.github.kerubistan.kerub.planner.steps.vm.start.kvm.KvmStartVirtualMachine
import org.junit.Assert
import org.junit.Test

class StepBenefitComparatorTest {

	private val virtualMachine = VirtualMachine(
			name = "vm1",
			expectations = listOf(
					VirtualMachineAvailabilityExpectation(
							up = true
					)
			)
	)

	private val host = Host(
			address = "host-1.example.com",
			publicKey = "",
			dedicated = true
	)

	private val state = OperationalState.fromLists(
			vms = listOf(virtualMachine),
			hosts = listOf(host),
			hostDyns = listOf(HostDynamic(
					id = host.id,
					status = HostStatus.Up
			))
	)

	private val start = KvmStartVirtualMachine(
			vm = virtualMachine,
			host = host
	)

	private val stopHost = PowerDownHost(
			host = host
	)

	@Test
	fun compare() {
		Assert.assertEquals(
				StepBenefitComparator(PlanViolationDetectorImpl(), Plan(state, listOf())).compare(start, start), 0)
		Assert.assertEquals(
				StepBenefitComparator(PlanViolationDetectorImpl(), Plan(state, listOf())).compare(stopHost, stopHost),
				0)
		Assert.assertTrue(
				StepBenefitComparator(PlanViolationDetectorImpl(), Plan(state, listOf())).compare(start, stopHost) < 0)
		Assert.assertTrue(
				StepBenefitComparator(PlanViolationDetectorImpl(), Plan(state, listOf())).compare(stopHost, start) > 0)
	}
}