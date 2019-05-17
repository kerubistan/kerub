package com.github.kerubistan.kerub.planner.issues.violations.vm

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.KB
import com.github.kerubistan.kerub.MB
import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.expectations.CacheSizeExpectation
import com.github.kerubistan.kerub.model.hardware.CacheInformation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testCpu
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVm
import io.github.kerubistan.kroki.time.now
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CacheSizeExpectationViolationDetectorTest {
	@Test
	fun check() {
		assertTrue("When the VM is not running, no hosts - should pass") {
			val expectation = CacheSizeExpectation(minL1 = 1.MB.toLong(), level = ExpectationLevel.DealBreaker)
			val vm = testVm.copy(
					expectations = listOf(
							expectation
					)
			)
			CacheSizeExpectationViolationDetector.check(
					vm,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm),
							vmDyns = listOf()
					)
			)
		}

		assertTrue("VM is running on a host with plenty of cache in the CPU") {
			val expectation = CacheSizeExpectation(minL1 = 1.MB.toLong(), level = ExpectationLevel.DealBreaker)
			val vm = testVm.copy(
					expectations = listOf(
							expectation
					)
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							cpus = listOf(
									testCpu.copy(
											l1cache = CacheInformation(
													socket = "A",
													size = 4.MB.toInt(),
													errorCorrection = "none",
													speedNs = 1600,
													operation = ""
											)
									)
							)
					)
			)
			CacheSizeExpectationViolationDetector.check(
					vm,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm),
							vmDyns = listOf(VirtualMachineDynamic(
									id = vm.id,
									status = VirtualMachineStatus.Up,
									hostId = host.id,
									memoryUsed = 1.GB,
									lastUpdated = now()
							)),
							hosts = listOf(host),
							hostDyns = listOf(HostDynamic(id = host.id, status = HostStatus.Up))
					)
			)
		}

		assertFalse("VM is running on a host with too little L1 cache") {
			val expectation = CacheSizeExpectation(minL1 = 1.MB.toLong(), level = ExpectationLevel.DealBreaker)
			val vm = testVm.copy(
					expectations = listOf(
							expectation
					)
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							cpus = listOf(
									testCpu.copy(
											l1cache = CacheInformation(
													socket = "A",
													size = 512.KB.toInt(),
													errorCorrection = "none",
													speedNs = 1600,
													operation = ""
											)
									)
							)
					)
			)
			CacheSizeExpectationViolationDetector.check(
					vm,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm),
							vmDyns = listOf(VirtualMachineDynamic(
									id = vm.id,
									status = VirtualMachineStatus.Up,
									hostId = host.id,
									memoryUsed = 1.GB,
									lastUpdated = now()
							)),
							hosts = listOf(host),
							hostDyns = listOf(HostDynamic(id = host.id, status = HostStatus.Up))
					)
			)
		}

	}

}