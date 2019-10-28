package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class HostDynamicTest : AbstractDataRepresentationTest<HostDynamic>() {
	override val testInstances = listOf(
			hostUp(testHost)
	)
	override val clazz = HostDynamic::class.java

	@Test
	fun validations() {
		assertThrows<IllegalStateException>("invalid memFree") {
			hostUp(testHost).copy(memFree = (-1).toBigInteger())
		}
		assertThrows<IllegalStateException>("invalid memSwapped") {
			hostUp(testHost).copy(memSwapped = (-1).toBigInteger())
		}
		assertThrows<IllegalStateException>("invalid memUsed") {
			hostUp(testHost).copy(memUsed = (-1).toBigInteger())
		}
	}
}