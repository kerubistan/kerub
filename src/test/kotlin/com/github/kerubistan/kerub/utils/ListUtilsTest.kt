package com.github.kerubistan.kerub.utils

import com.github.kerubistan.kerub.model.Entity
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ListUtilsTest {
	@Test
	fun skip() {
		assertEquals(listOf("B", "C"), listOf("A", "B", "C").skip())
		assertEquals(listOf("C"), listOf("A", "B", "C").skip().skip())
		assertEquals(listOf(), listOf<String>().skip())
		assertEquals(listOf(), listOf("A").skip())
	}

	data class TestEntity(override val id: Int, val name: String) : Entity<Int>

	@Test
	fun toMapEntities() {
		assertEquals(
				mapOf(
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

	data class Hat(val color: String)
	abstract class Creature {
		abstract val weight: Double
	}

	data class Human(val name: String, override val weight: Double, val favoriteColor: String, val hat: Hat? = null) : Creature()
	data class Crocodile(val id: String, override val weight: Double) : Creature()

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

	@Test
	fun subLists() {
		assertEquals(
				listOf(listOf("spam egg", "spam spam", "spam")),
				listOf("egg", "bacon", "foo", "spam egg", "spam spam", "spam", "egg", "bacon").subLists(1) {
					it.contains("spam")
				}
		)

		assertEquals(
				listOf(listOf("spam egg", "spam spam", "spam")),
				listOf("egg", "spam", "bacon", "foo", "spam egg", "spam spam", "spam", "egg", "bacon").subLists(1) {
					it.contains("spam")
				}
		)

	}

	@Test
	fun anyInstanceOf() {
		assertTrue(listOf("A", 1, true).any<String>())
		assertFalse(listOf("A", 1, true).any<List<String>>())
		assertFalse(listOf<Any>().any<String>())
	}

	@Test
	fun anyInstanceWithPredicate() {
		assertTrue(listOf("A", 1, "B").hasAny<String> { it.startsWith("A") })
		assertFalse(listOf("A", 1).hasAny<Boolean> { it })
	}

	@Test
	fun hasNone() {
		assertTrue(listOf("A", 1, "B", 2).hasNone<Int> { it > 3 })
		assertTrue(listOf("A", 1, "B", 2).hasNone(String::isEmpty))
		assertFalse(listOf("A", 1, "B", 2).hasNone<String> { it.startsWith("A") })
	}

	@Test
	fun noInstanceOf() {
		assertFalse(listOf("A", 1, true).none<String>())
		assertTrue(listOf("A", 1, true).none<List<String>>())
		assertTrue(listOf<Any>().none<String>())
	}

	@Test
	fun mapInstances() {
		assertEquals(listOf("A", 1, "B", 2).mapInstances<Int, Int> { it + 1 }, listOf(2, 3))
		assertEquals(listOf("A", 1, "B", 2).mapInstances<String, Int> { it.length }, listOf(1, 1))
		assertEquals(listOf("A", 1, "B", 2).mapInstances<String, String> { if (it == "B") "X" else null }, listOf("X"))
	}

	@Test
	fun contains() {
		assertFalse("something is not in null") { "something" in null }
		assertFalse("something is not in empty set") { "something" in setOf<String>() }
		assertFalse("something is not in empty set") { "something" in setOf("something else") }
		assertTrue("something is in empty set") { "something" in setOf("something") }
	}
}