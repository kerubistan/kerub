package com.github.K0zka.kerub.utils

import com.github.K0zka.kerub.model.Entity
import org.junit.Test
import kotlin.test.assertEquals

class ListUtilsTest {
	@Test
	fun skip() {
		assertEquals(listOf("B", "C"), listOf("A", "B", "C").skip())
		assertEquals(listOf("C"), listOf("A", "B", "C").skip().skip())
		assertEquals(listOf<String>(), listOf<String>().skip())
		assertEquals(listOf<String>(), listOf("A").skip())
	}

	@Test
	fun join() {
		assertEquals(listOf("A", "B", "C", "D"), listOf(listOf("A"), listOf("B", "C", "D")).join())
		assertEquals(listOf("A", "B", "C", "D"), listOf(listOf("A", "B"), listOf("C", "D")).join())
		assertEquals(listOf<String>(), listOf(listOf<String>(), listOf<String>()).join())
	}

	@Test
	fun toMap() {
		assertEquals(mapOf(1 to "1", 2 to "2", 3 to "3"), listOf("1", "2", "3").toMap { it.toInt() })
	}

	data class TestEntity(override val id: Int, val name: String) : Entity<Int>

	@Test
	fun toMapEntities() {
		assertEquals(mapOf(
				1 to TestEntity(1, "FOO"),
				2 to TestEntity(2, "BAR"),
				3 to TestEntity(3, "BAZ")
		), listOf(
				TestEntity(1, "FOO"),
				TestEntity(2, "BAR"),
				TestEntity(3, "BAZ")
		).toMap())
	}

	@Test
	fun times() {
		assertEquals(listOf("A" to 1, "A" to 2, "B" to 1, "B" to 2), listOf("A", "B") * listOf(1, 2))
		assertEquals(listOf(), listOf<String>() * listOf(1, 2))
		assertEquals(listOf(), listOf("A", "B") * (listOf<Any>()))
		assertEquals(listOf(), listOf<String>() * (listOf<Int>()))
	}
}