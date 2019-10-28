package com.github.kerubistan.kerub.model.dynamic.gvinum

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class ConcatenatedGvinumConfigurationTest : AbstractDataRepresentationTest<ConcatenatedGvinumConfiguration>() {
	override val testInstances = listOf(
			ConcatenatedGvinumConfiguration(disks = mapOf("test-disk-1" to 1.TB, "test-disk-2" to 2.TB))
	)
	override val clazz = ConcatenatedGvinumConfiguration::class.java

	@Test
	fun validations() {
		assertThrows<IllegalStateException> {
			ConcatenatedGvinumConfiguration(disks = mapOf())
		}
	}
}