package com.github.kerubistan.kerub.planner.issues.problems.vms

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class VmOnRecyclingHostDetectorTest {
	@Test
	fun detect() {
		assertTrue("blank state - no problem") {
			VmOnRecyclingHostDetector.detect(Plan(state = OperationalState.fromLists())).isEmpty()
		}

		assertTrue("vm on working host - no problem") {
			val vm = testVm
			val host = testHost
			val hostToBeRemoved = testHost.copy(
					id = UUID.randomUUID(),
					recycling = true
			)

			VmOnRecyclingHostDetector.detect(
					Plan(state = OperationalState.fromLists(
							vms = listOf(vm),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = vm.id,
											hostId = host.id,
											status = VirtualMachineStatus.Up,
											memoryUsed = 1.GB
									)
							),
							hosts = listOf(host, hostToBeRemoved),
							hostDyns = listOf(
									HostDynamic(
											id = host.id,
											status = HostStatus.Up
									),
									HostDynamic(
											id = hostToBeRemoved.id,
											status = HostStatus.Up
									)
							)
					))
			).isEmpty()
		}

		assertTrue("vm on recycling host - detect problem") {
			val vm = testVm
			val host = testHost
			val hostToBeRemoved = testHost.copy(
					id = UUID.randomUUID(),
					recycling = true,
					address = "shipwreck.example.com"
			)

			VmOnRecyclingHostDetector.detect(
					Plan(state = OperationalState.fromLists(
							vms = listOf(vm),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = vm.id,
											hostId = hostToBeRemoved.id,
											status = VirtualMachineStatus.Up,
											memoryUsed = 1.GB
									)
							),
							hosts = listOf(host, hostToBeRemoved),
							hostDyns = listOf(
									HostDynamic(
											id = host.id,
											status = HostStatus.Up
									),
									HostDynamic(
											id = hostToBeRemoved.id,
											status = HostStatus.Up
									)
							)
					))
			) == listOf(VmOnRecyclingHost(vm = vm, host = hostToBeRemoved))
		}

	}
}