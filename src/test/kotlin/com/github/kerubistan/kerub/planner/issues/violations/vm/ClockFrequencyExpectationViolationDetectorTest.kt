package com.github.kerubistan.kerub.planner.issues.violations.vm

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.expectations.ClockFrequencyExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testCpu
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVm
import io.github.kerubistan.kroki.time.now
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ClockFrequencyExpectationViolationDetectorTest {
	@Test
	fun check() {
		assertTrue("Not running vm") {
			val expectation = ClockFrequencyExpectation(
					minimalClockFrequency = 1800,
					level = ExpectationLevel.DealBreaker
			)
			val vm = testVm.copy(expectations = listOf(expectation))
			ClockFrequencyExpectationViolationDetector.check(
					vm,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm)
					)
			)
		}
		assertTrue("VM running on a OK host") {
			val expectation = ClockFrequencyExpectation(
					minimalClockFrequency = 1800,
					level = ExpectationLevel.DealBreaker
			)
			val vm = testVm.copy(expectations = listOf(expectation))
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							cpus = listOf(
									testCpu.copy(
											maxSpeedMhz = 3200
									)
							)
					)
			)
			ClockFrequencyExpectationViolationDetector.check(
					vm,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id =vm.id,
											status = VirtualMachineStatus.Up,
											lastUpdated = now(),
											memoryUsed = 1.GB,
											hostId = host.id)
							),
							hosts = listOf(host),
							hostDyns = listOf(
									HostDynamic(id = host.id, status = HostStatus.Up, memFree = 16.GB)
							)
					)
			)
		}
		assertFalse("VM running on a slow host") {
			val expectation = ClockFrequencyExpectation(
					minimalClockFrequency = 3000,
					level = ExpectationLevel.DealBreaker
			)
			val vm = testVm.copy(expectations = listOf(expectation))
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							cpus = listOf(
									testCpu.copy(
											maxSpeedMhz = 1800
									)
							)
					)
			)
			ClockFrequencyExpectationViolationDetector.check(
					vm,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id =vm.id,
											status = VirtualMachineStatus.Up,
											lastUpdated = now(),
											memoryUsed = 1.GB,
											hostId = host.id)
							),
							hosts = listOf(host),
							hostDyns = listOf(
									HostDynamic(id = host.id, status = HostStatus.Up, memFree = 16.GB)
							)
					)
			)
		}

	}
}