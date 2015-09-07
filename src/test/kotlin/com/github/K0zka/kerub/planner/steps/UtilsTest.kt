package com.github.K0zka.kerub.planner.steps

import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

public class UtilsTest {
	Test
	fun collectionReplace() {
		val original = listOf("A", "b", "c")
		val replacement = original.replace({it == "A"}, { it.toLowerCase() })

		Assert.assertThat(replacement, CoreMatchers.hasItem("b"))
		Assert.assertThat(replacement, CoreMatchers.hasItem("c"))
		Assert.assertThat(replacement, CoreMatchers.not(CoreMatchers.hasItem("A")))
		Assert.assertThat(replacement, CoreMatchers.hasItem("a"))
	}
}