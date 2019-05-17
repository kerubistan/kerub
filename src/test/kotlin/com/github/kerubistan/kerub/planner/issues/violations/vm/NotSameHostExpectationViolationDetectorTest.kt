package com.github.kerubistan.kerub.planner.issues.violations.vm

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.expectations.NotSameHostExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import io.github.kerubistan.kroki.time.now
import org.junit.Test
import java.util.UUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NotSameHostExpectationViolationDetectorTest {
	@Test
	fun check() {
		assertTrue("VMs not running - should be fine") {
			val vm2 = testVm.copy(
					id = UUID.randomUUID()
			)
			val expectation = NotSameHostExpectation(otherVmId = vm2.id)
			val vm1 = testVm.copy(
					id = UUID.randomUUID(),
					expectations = listOf(expectation)
			)
			val host = testHost.copy()
			NotSameHostExpectationViolationDetector.check(
					vm1,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm1, vm2),
							hosts = listOf(host)
					)
			)
		}

		assertTrue("Other VM not running - should be fine") {
			val vm2 = testVm.copy(
					id = UUID.randomUUID()
			)
			val expectation = NotSameHostExpectation(otherVmId = vm2.id)
			val vm1 = testVm.copy(
					id = UUID.randomUUID(),
					expectations = listOf(expectation)
			)
			val host = testHost.copy()
			NotSameHostExpectationViolationDetector.check(
					vm1,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm1, vm2),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = vm1.id,
											status = VirtualMachineStatus.Up,
											hostId = host.id,
											lastUpdated = now(),
											memoryUsed = 1.GB
									)
							),
							hosts = listOf(host),
							hostDyns = listOf()
					)
			)
		}

		assertTrue("Other VM running on other host - should be fine") {
			val vm2 = testVm.copy(
					id = UUID.randomUUID()
			)
			val expectation = NotSameHostExpectation(otherVmId = vm2.id)
			val vm1 = testVm.copy(
					id = UUID.randomUUID(),
					expectations = listOf(expectation)
			)
			val host1 = testHost.copy(
					id = UUID.randomUUID()
			)
			val host2 = testHost.copy(
					id = UUID.randomUUID()
			)
			NotSameHostExpectationViolationDetector.check(
					vm1,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm1, vm2),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = vm1.id,
											status = VirtualMachineStatus.Up,
											hostId = host1.id,
											lastUpdated = now(),
											memoryUsed = 1.GB
									),
									VirtualMachineDynamic(
											id = vm2.id,
											status = VirtualMachineStatus.Up,
											hostId = host2.id,
											lastUpdated = now(),
											memoryUsed = 1.GB
									)
							),
							hosts = listOf(host1, host2),
							hostDyns = listOf(
									HostDynamic(
											id = host1.id,
											status = HostStatus.Up
									),
									HostDynamic(
											id = host2.id,
											status = HostStatus.Up
									)
							)
					)
			)
		}

		assertFalse("VMs on the same host - should be a violation") {
			val vm2 = testVm.copy(
					id = UUID.randomUUID()
			)
			val expectation = NotSameHostExpectation(otherVmId = vm2.id)
			val vm1 = testVm.copy(
					id = UUID.randomUUID(),
					expectations = listOf(expectation)
			)
			val host1 = testHost.copy(
					id = UUID.randomUUID()
			)
			NotSameHostExpectationViolationDetector.check(
					vm1,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm1, vm2),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = vm1.id,
											status = VirtualMachineStatus.Up,
											hostId = host1.id,
											lastUpdated = now(),
											memoryUsed = 1.GB
									),
									VirtualMachineDynamic(
											id = vm2.id,
											status = VirtualMachineStatus.Up,
											hostId = host1.id,
											lastUpdated = now(),
											memoryUsed = 1.GB
									)
							),
							hosts = listOf(host1),
							hostDyns = listOf(
									HostDynamic(
											id = host1.id,
											status = HostStatus.Up
									)
							)
					)
			)
		}

	}

}