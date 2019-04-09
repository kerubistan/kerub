package com.github.kerubistan.kerub.planner.steps

import com.github.kerubistan.kerub.utils.createObjectMapper
import org.junit.Ignore
import org.junit.Test

abstract class AbstractOperationalStepVerifications(private val step: AbstractOperationalStep) {

	companion object {
		private val mapper = createObjectMapper(true)
	}

	@Ignore("See https://youtrack.jetbrains.com/issue/KT-22923 " +
			"and https://github.com/FasterXML/jackson-module-kotlin/issues/88")
	@Test
	fun jsonSerialize() {
		mapper.writeValueAsString(step)
	}
}