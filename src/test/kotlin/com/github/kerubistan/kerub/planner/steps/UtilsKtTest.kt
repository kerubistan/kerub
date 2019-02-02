package com.github.kerubistan.kerub.planner.steps

import org.junit.Test
import kotlin.test.assertTrue

class UtilsKtTest {

	@Test
	fun factoryFeature() {
		assertTrue("disabled features always produce empty list") {
			factoryFeature(false) { listOf("a", "b") }.isEmpty()
		}

		assertTrue("enabled features may produce something else") {
			factoryFeature(true) { listOf("a", "b") } == listOf("a", "b")
		}
	}
}