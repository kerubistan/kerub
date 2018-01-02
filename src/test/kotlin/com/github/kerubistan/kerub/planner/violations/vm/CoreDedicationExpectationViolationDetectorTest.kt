package com.github.kerubistan.kerub.planner.violations.vm

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.expectations.CoreDedicationExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testCpu
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.now
import org.junit.Test
import java.util.UUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoreDedicationExpectationViolationDetectorTest {
	@Test
	fun check() {
		assertTrue("Not running VM") {
			val vm = testVm.copy()
			CoreDedicationExpectationViolationDetector.check(
					vm,
					CoreDedicationExpectation(level = ExpectationLevel.DealBreaker),
					OperationalState.fromLists(
							vms = listOf(vm)
					)
			)
		}
		assertTrue("Single VM on the server with enough cores") {
			val expectation = CoreDedicationExpectation(level = ExpectationLevel.DealBreaker)
			val vm = testVm.copy(
					expectations = listOf(expectation)
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							cpus = listOf(
									testCpu.copy(
											coreCount = 4
									)
							)
					)
			)
			CoreDedicationExpectationViolationDetector.check(
					vm,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = vm.id,
											hostId = host.id,
											status = VirtualMachineStatus.Up,
											memoryUsed = 1.GB
									)
							),
							hosts = listOf(host),
							hostDyns = listOf(
									HostDynamic(
											id = host.id,
											status = HostStatus.Up,
											lastUpdated = now(),
											memFree = 16.GB
									)
							)
					)
			)
		}
		assertFalse("Over-allocated VMs on the server") {
			val expectation = CoreDedicationExpectation(level = ExpectationLevel.DealBreaker)
			val vm1 = testVm.copy(
					id = UUID.randomUUID(),
					nrOfCpus = 1,
					expectations = listOf(expectation)
			)
			val vm2 = testVm.copy(
					id = UUID.randomUUID(),
					nrOfCpus = 16
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							cpus = listOf(
									testCpu.copy(
											coreCount = 4
									)
							)
					)
			)
			CoreDedicationExpectationViolationDetector.check(
					vm1,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm1, vm2),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = vm1.id,
											status = VirtualMachineStatus.Up,
											lastUpdated = now(),
											memoryUsed = 1.GB,
											hostId = host.id
									),
									VirtualMachineDynamic(
											id = vm2.id,
											status = VirtualMachineStatus.Up,
											lastUpdated = now(),
											memoryUsed = 1.GB,
											hostId = host.id
									)

							),
							hosts = listOf(host),
							hostDyns = listOf(
									HostDynamic(
											id = host.id,
											memFree = 16.GB,
											lastUpdated = now(),
											status = HostStatus.Up
									)
							)
					)
			)
		}

	}

}