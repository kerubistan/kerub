package com.github.kerubistan.kerub.planner.issues.violations.vm

import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.time.now
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VirtualMachineAvailabilityExpectationViolationDetectorTest {
	@Test
	fun check() {
		assertTrue("VM is up") {
			val expectation = VirtualMachineAvailabilityExpectation(up = true)
			val host = testHost.copy()
			val vm = testVm.copy(
					expectations = listOf(expectation)
			)

			VirtualMachineAvailabilityExpectationViolationDetector.check(
					vm,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = vm.id,
											status = VirtualMachineStatus.Up,
											memoryUsed = 1.GB,
											lastUpdated = now(),
											hostId = host.id
									)
							),
							hosts = listOf(host),
							hostDyns = listOf(
									HostDynamic(
											id = host.id,
											status = HostStatus.Up
									)
							)
					)
			)
		}

		assertFalse("VM is down") {
			val expectation = VirtualMachineAvailabilityExpectation(up = true)
			val host = testHost.copy()
			val vm = testVm.copy(
					expectations = listOf(expectation)
			)

			VirtualMachineAvailabilityExpectationViolationDetector.check(
					vm,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm),
							vmDyns = listOf(),
							hosts = listOf(host),
							hostDyns = listOf(
									HostDynamic(
											id = host.id,
											status = HostStatus.Up
									)
							)
					)
			)
		}

	}

}