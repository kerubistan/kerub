package com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testOtherHost
import org.junit.Test
import kotlin.test.assertTrue

class CpuOverheatDetectorTest {

	@Test
	fun detect() {
		assertTrue("blank state, no problem") {
			CpuOverheatDetector.detect(Plan(OperationalState.fromLists())).isEmpty()
		}
		assertTrue("Two hosts, one overheating - detect it") {
			CpuOverheatDetector.detect(
					Plan(
							OperationalState.fromLists(
									hosts = listOf(testHost, testOtherHost),
									hostDyns = listOf(
											hostUp(testHost).copy(
													cpuTemperature = listOf(100) // very hot
											),
											hostUp(testOtherHost)
									)
							))
			).single().let {
				it.cores == mapOf(0 to 100)
			}
		}
	}
}