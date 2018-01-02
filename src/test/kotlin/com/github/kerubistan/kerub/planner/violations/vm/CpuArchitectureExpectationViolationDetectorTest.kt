package com.github.kerubistan.kerub.planner.violations.vm

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.expectations.CpuArchitectureExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.now
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CpuArchitectureExpectationViolationDetectorTest {
	@Test
	fun check() {
		assertTrue("Not running VM must pass") {
			val expectation = CpuArchitectureExpectation(cpuArchitecture = "x86_64",
														 level = ExpectationLevel.DealBreaker)
			val vm = testVm.copy(
					expectations = listOf(expectation)
			)
			CpuArchitectureExpectationViolationDetector.check(vm, expectation,
															  OperationalState.fromLists(vms = listOf(vm)))
		}
		assertFalse("VM running on a PPC wit X86_64 requirement must create violation") {
			val expectation = CpuArchitectureExpectation(cpuArchitecture = "x86_64",
														 level = ExpectationLevel.DealBreaker)
			val vm = testVm.copy(
					expectations = listOf(expectation)
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							cpuArchitecture = "PPC"
					)
			)
			val state = OperationalState.fromLists(
					vms = listOf(vm),
					hosts = listOf(host),
					vmDyns = listOf(
							VirtualMachineDynamic(id = vm.id, status = VirtualMachineStatus.Up, lastUpdated = now(),
												  memoryUsed = 1.GB, hostId = host.id)
					),
					hostDyns = listOf(
						HostDynamic(id = host.id, lastUpdated = now(), status = HostStatus.Up)
					)
			)
			CpuArchitectureExpectationViolationDetector.check(vm, expectation, state)
		}
	}

}