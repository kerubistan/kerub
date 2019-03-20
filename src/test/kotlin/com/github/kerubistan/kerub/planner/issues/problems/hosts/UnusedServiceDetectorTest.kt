package com.github.kerubistan.kerub.planner.issues.problems.hosts

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.common.AbstractProblemDetectorVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import org.junit.Test
import kotlin.test.assertTrue

class UnusedServiceDetectorTest : AbstractProblemDetectorVerifications(UnusedServiceDetector) {
	@Test
	fun detect() {
		assertTrue("no services - no problem") {
			val host = testHost.copy()
			val vm = testVm.copy()
			UnusedServiceDetector.detect(
					Plan(state = OperationalState.fromLists(
							vms = listOf(vm),
							vmDyns = listOf(),
							hosts = listOf(host),
							hostCfgs = listOf()
					))
			).isEmpty()
		}

		assertTrue("Actually unused service - must be detected") {
			UnusedServiceDetector.detect(
					Plan(state = OperationalState.fromLists(
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(),
							vms = listOf(testVm),
							vmDyns = listOf(),
							hosts = listOf(testHost),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											services = listOf(IscsiService(vstorageId = testDisk.id, password = "blah"))
									)
							)
					))
			) == listOf(
					UnusedService(host = testHost, service = IscsiService(vstorageId = testDisk.id, password = "blah")))
		}

	}

}