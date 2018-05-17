package com.github.kerubistan.kerub.planner.issues.problems.vstorage

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.testDisk
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertTrue

internal class RecyclingStorageDeviceDetectorTest {

	@Test
	fun detect() {
		assertTrue("blank state - no problem") {
			RecyclingStorageDeviceDetector.detect(
					Plan(
							state = OperationalState.fromLists()
					)
			).isEmpty()
		}
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