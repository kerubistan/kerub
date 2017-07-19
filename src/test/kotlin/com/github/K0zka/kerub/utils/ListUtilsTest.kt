package com.github.K0zka.kerub.utils

import com.github.K0zka.kerub.model.Entity
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

	@Test
	fun update() {
		data class Employee(val id: Int, val name: String, val role: String, val salary: Int)
		data class Promotion(val name: String, val newRole: String, val raise: Int)

		val results = listOf(
				Employee(id = 1, name = "Bob", role = "Bloatware Engineer", salary = 100),
				Employee(id = 2, name = "Mike", role = "Hardware Hammerer", salary = 110),
				Employee(id = 3, name = "Jack", role = "Outlook Manager", salary = 600)
		).update(
				updateList = listOf(
						Promotion(name = "Bob", newRole = "Senior Bloatware Engineer", raise = 10),
						Promotion(name = "Jack", newRole = "Document Wizard", raise = -10),
						Promotion(name = "Tronald Dump", newRole = "President", raise = 100000)
				),
				selfKey = { employee -> employee.name },
				upKey = { (name) -> name },
				merge = { employee, promotion ->
					employee.copy(
							role = promotion.newRole,
							salary = employee.salary + promotion.raise
					)
				}
		)

		assertTrue(results.contains(Employee(id = 1, name = "Bob", role = "Senior Bloatware Engineer", salary = 110)))
		assertTrue(results.contains(Employee(id = 2, name = "Mike", role = "Hardware Hammerer", salary = 110)))
		assertTrue(results.contains(Employee(id = 3, name = "Jack", role = "Document Wizard", salary = 590)))
		assertEquals(3, results.size)
	}
}