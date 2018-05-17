package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.unallocate

import com.github.kerubistan.kerub.planner.OperationalState
import org.junit.Test
import kotlin.test.assertTrue

class UnAllocateLvFactoryTest {
	@Test
	fun produce() {
		assertTrue("blank state - no steps") {
			UnAllocateLvFactory.produce(OperationalState.fromLists()).isEmpty()
		}

	}
}