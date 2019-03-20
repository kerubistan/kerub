package com.github.kerubistan.kerub.planner.issues.problems.vstorage

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.common.AbstractProblemDetectorVerifications
import com.github.kerubistan.kerub.testDisk
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class RecyclingStorageDeviceDetectorTest : AbstractProblemDetectorVerifications(RecyclingStorageDeviceDetector) {

	@Test
	fun detect() {
		assertTrue("one recycling storage - must be detected") {
			val notRecycling = testDisk.copy(id = UUID.randomUUID())
			val recycling = testDisk.copy(id = UUID.randomUUID(), recycling = true)
			RecyclingStorageDeviceDetector.detect(
					Plan(
							state = OperationalState.fromLists(vStorage = listOf(recycling, notRecycling))
					)
			) == listOf(RecyclingStorageDevice(recycling))
		}

	}
}