package com.github.kerubistan.kerub.planner.steps

import io.github.kerubistan.kroki.collections.replace
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

class UtilsTest {
	@Test
	fun collectionReplace() {
		val original = listOf("A", "b", "c")
		val replacement = original.replace({ it == "A" }, { it.toLowerCase() })

		Assert.assertThat(replacement, CoreMatchers.hasItem("b"))
		Assert.assertThat(replacement, CoreMatchers.hasItem("c"))
		Assert.assertThat(replacement, CoreMatchers.not(CoreMatchers.hasItem("A")))
		Assert.assertThat(replacement, CoreMatchers.hasItem("a"))
	}

}