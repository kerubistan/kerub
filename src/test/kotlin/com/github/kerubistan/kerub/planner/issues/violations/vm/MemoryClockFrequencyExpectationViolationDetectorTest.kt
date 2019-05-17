package com.github.kerubistan.kerub.planner.issues.violations.vm

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.expectations.MemoryClockFrequencyExpectation
import com.github.kerubistan.kerub.model.hardware.MemoryInformation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVm
import io.github.kerubistan.kroki.time.now
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MemoryClockFrequencyExpectationViolationDetectorTest {
	@Test
	fun check() {
		assertTrue("Not running VM - expectation is met") {
			val expectation = MemoryClockFrequencyExpectation(level = ExpectationLevel.Want, min = 1600)
			val vm = testVm.copy()
			MemoryClockFrequencyExpectationViolationDetector.check(
					vm,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm),
							hosts = listOf(testHost)
					)
			)
		}

		assertTrue("VM running on a good host") {
			val expectation = MemoryClockFrequencyExpectation(level = ExpectationLevel.Want, min = 1600)
			val vm = testVm.copy()
			val host = testHost.copy()
			MemoryClockFrequencyExpectationViolationDetector.check(
					vm,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm),
							hosts = listOf(testHost)
					)
			)
		}

		assertFalse("VM running on a slow host") {
			val expectation = MemoryClockFrequencyExpectation(level = ExpectationLevel.Want, min = 1600)
			val vm = testVm.copy()
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							memoryDevices = listOf(
									MemoryInformation(
											size = 4.GB,
											manufacturer = "TEST",
											speedMhz = 1200,
											bankLocator = "TEST",
											formFactor = "SODIMM",
											locator = "",
											type = "",
											serialNumber = "",
											partNumber = "",
											configuredSpeedMhz = null
									)
							)
					)
			)
			MemoryClockFrequencyExpectationViolationDetector.check(
					vm,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = vm.id,
											status = VirtualMachineStatus.Up,
											hostId = host.id,
											lastUpdated = now(),
											memoryUsed = 1.GB
									)
							),
							hosts = listOf(host),
							hostDyns = listOf(
									HostDynamic(
											id = vm.id,
											status = HostStatus.Up
									)
							)
					)
			)
		}

	}

}