package com.github.kerubistan.kerub.planner.steps.vstorage.fs.convert.othercap

import com.github.kerubistan.kerub.planner.OperationalState
import org.junit.Test
import kotlin.test.assertTrue

class ConvertImageFactoryTest {

	@Test
	fun produce() {
		assertTrue("blank state should not produce steps") {
			ConvertImageFactory.produce(OperationalState.fromLists()).isEmpty()
		}
	}
}