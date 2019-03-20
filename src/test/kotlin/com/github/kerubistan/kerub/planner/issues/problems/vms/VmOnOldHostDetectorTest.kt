package com.github.kerubistan.kerub.planner.issues.problems.vms

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.common.AbstractProblemDetectorVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.vmUp
import org.junit.Test
import java.util.Date
import kotlin.test.assertTrue

class VmOnOldHostDetectorTest : AbstractProblemDetectorVerifications(VmOnOldHostDetector) {

	@Test
	fun detect() {
		assertTrue("detect vm on old host") {
			val oldHost = testHost.copy(
					endOfPlannedLifetime = Date(System.currentTimeMillis() - 1)
			)
			VmOnOldHostDetector.detect(Plan(OperationalState.fromLists(
					hosts = listOf(oldHost),
					hostDyns = listOf(hostUp(oldHost)),
					vms = listOf(testVm),
					vmDyns = listOf(vmUp(testVm, oldHost))
			))).isNotEmpty()
		}
		assertTrue("host is not old yet") {
			val oldHost = testHost.copy(
					endOfPlannedLifetime = Date(System.currentTimeMillis() + 10000000)
			)
			VmOnOldHostDetector.detect(Plan(OperationalState.fromLists(
					hosts = listOf(oldHost),
					hostDyns = listOf(hostUp(oldHost)),
					vms = listOf(testVm),
					vmDyns = listOf(vmUp(testVm, oldHost))
			))).isEmpty()
		}
	}
}