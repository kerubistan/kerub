package com.github.kerubistan.kerub.planner.steps.vstorage.fs.unallocate

import com.github.kerubistan.kerub.planner.OperationalState
import org.junit.Test
import kotlin.test.assertTrue

class UnAllocateFsFactoryTest {
	@Test
	fun produce() {
		assertTrue("blank state, no steps should be produced") {
			UnAllocateFsFactory.produce(OperationalState.fromLists()) == listOf<UnAllocateFs>()
		}
	}
}