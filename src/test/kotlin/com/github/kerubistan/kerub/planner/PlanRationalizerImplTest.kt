package com.github.kerubistan.kerub.planner

import com.github.k0zka.finder4j.backtrack.StepFactory
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.vm.start.kvm.KvmStartVirtualMachine
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon.StartNfsDaemon
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon.StopNfsDaemon
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import kotlin.test.assertTrue

class PlanRationalizerImplTest {

	@Test
	fun tryRemoveInverses() {
		val vm = testVm.copy(expectations = listOf(VirtualMachineAvailabilityExpectation()))
		val startVirtualMachine = KvmStartVirtualMachine(vm = vm, host = testHost)
		assertTrue("remove the fully irrelevant steps from after and before") {
			val plan = Plan.planBy(
					initial = OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
							vms = listOf(vm)
					),
					steps = listOf(
							StartNfsDaemon(host = testHost),
							startVirtualMachine,
							StopNfsDaemon(host = testHost)
					)
			)

			val rational = PlanRationalizerImpl(mock(), mock(), mock()).tryRemoveInverses(plan)
			rational.steps == listOf(startVirtualMachine)
		}

		assertTrue("remove the fully irrelevant steps from before") {
			val plan = Plan.planBy(
					initial = OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
							vms = listOf(vm)
					),
					steps = listOf(
							StartNfsDaemon(host = testHost),
							StopNfsDaemon(host = testHost),
							startVirtualMachine
					)
			)

			val rational = PlanRationalizerImpl(mock(), mock(), mock()).tryRemoveInverses(plan)
			rational.steps == listOf(startVirtualMachine)
		}

		assertTrue("remove the fully irrelevant steps from after") {
			val plan = Plan.planBy(
					initial = OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
							vms = listOf(vm)
					),
					steps = listOf(
							startVirtualMachine,
							StartNfsDaemon(host = testHost),
							StopNfsDaemon(host = testHost)
					)
			)

			val rational = PlanRationalizerImpl(mock(), mock(), mock()).tryRemoveInverses(plan)
			rational.steps == listOf(startVirtualMachine)
		}

	}

	@Test
	fun rationalize() {
		assertTrue("there is not much left to rationalize on a blank plan") {
			val blank = Plan(
					states = listOf(),
					steps = listOf()
			)
			PlanRationalizerImpl(mock(), mock(), mock()).rationalize(blank) == blank
		}

		assertTrue("there is nothing to rationalize on a single step solution either") {
			val vm = testVm.copy(expectations = listOf(VirtualMachineAvailabilityExpectation()))
			val onestep = Plan(
					states = listOf(
							OperationalState.fromLists(
									hosts = listOf(testHost),
									hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
									vms = listOf(vm)
							)
					),
					steps = listOf(KvmStartVirtualMachine(vm = vm, host = testHost))
			)
			PlanRationalizerImpl(mock(), mock(), mock()).rationalize(onestep) == onestep
		}

		assertTrue("remove totally pointless step to start nfs server") {
			val stepFactory = mock<StepFactory<AbstractOperationalStep, Plan>>()
			val vm = testVm.copy(expectations = listOf(VirtualMachineAvailabilityExpectation()))
			val startVirtualMachine = KvmStartVirtualMachine(vm = vm, host = testHost)
			whenever(stepFactory.produce(any())).thenReturn(listOf(startVirtualMachine))
			val plan = Plan.planBy(
					initial = OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
							vms = listOf(vm)
					),
					steps = listOf(
							StartNfsDaemon(host = testHost),
							startVirtualMachine,
							StopNfsDaemon(host = testHost)
					)
			)
			PlanRationalizerImpl(stepFactory, mock(), mock()).rationalize(plan).steps == listOf(
					KvmStartVirtualMachine(vm = vm, host = testHost)
			)
		}

	}
}