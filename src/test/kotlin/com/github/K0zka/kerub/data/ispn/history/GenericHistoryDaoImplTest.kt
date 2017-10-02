package com.github.K0zka.kerub.data.ispn.history

import com.github.K0zka.kerub.model.Range
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.history.ChangeEvent
import com.github.K0zka.kerub.model.history.HistorySummary
import com.github.K0zka.kerub.model.history.NumericPropertyChangeSummary
import com.github.K0zka.kerub.model.history.PropertyChange
import com.github.K0zka.kerub.testHost
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Random
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GenericHistoryDaoImplTest {
	@Test
	fun changesOfProperty() {
		val changes = GenericHistoryDaoImpl.changesOfProperty(
				"foo",
				listOf(
						ChangeEvent(
								entityKey = testHost.id,
								changes = listOf(
										PropertyChange("foo", 1, 2),
										PropertyChange("bar", 2, 3)
								),
								appVersion = ""
						),
						ChangeEvent(
								entityKey = testHost.id,
								changes = listOf(
										PropertyChange("bar", 2, 3)
								),
								appVersion = ""
						),
						HistorySummary(
								appVersion = "",
								changes = listOf(
										NumericPropertyChangeSummary(
												property = "foo",
												min = 1,
												max = 5,
												average = BigDecimal(3),
												extremes = listOf()
										)
								),
								time = Range(10, 1000),
								entityKey = testHost.id
						),
						HistorySummary(
								appVersion = "",
								changes = listOf(
										NumericPropertyChangeSummary(
												property = "bar",
												min = 1,
												max = 5,
												average = BigDecimal(3),
												extremes = listOf()
										)
								),
								time = Range(1000, 100000),
								entityKey = testHost.id
						)
				)
		)

		assertEquals(2, changes.size)
	}

	@Test
	fun changedPropertyNames() {
		val names = GenericHistoryDaoImpl.changedPropertyNames(
				listOf(
						ChangeEvent(
								entityKey = testHost.id,
								changes = listOf(
										PropertyChange("foo", 1, 2),
										PropertyChange("bar", 2, 3)
								),
								appVersion = ""
						),
						ChangeEvent(
								entityKey = testHost.id,
								changes = listOf(
										PropertyChange("bar", 2, 3)
								),
								appVersion = ""
						),
						HistorySummary(
								appVersion = "",
								changes = listOf(
										NumericPropertyChangeSummary(
												property = "foo",
												min = 1,
												max = 5,
												average = BigDecimal(3),
												extremes = listOf()
										)
								),
								time = Range(10, 1000),
								entityKey = testHost.id
						),
						HistorySummary(
								appVersion = "",
								changes = listOf(
										NumericPropertyChangeSummary(
												property = "bar",
												min = 1,
												max = 5,
												average = BigDecimal(3),
												extremes = listOf()
										)
								),
								time = Range(1000, 100000),
								entityKey = testHost.id
						)
				)

		)

		assertEquals(2, names.size)
		assertTrue(names.contains("foo"))
		assertTrue(names.contains("bar"))
	}

	@Test
	fun isNumberField() {
		assertTrue {
			GenericHistoryDaoImpl.isNumber(
					GenericHistoryDaoImpl.getPropertyType(HostDynamic::idleCpu.name, HostDynamic::class)!!
			)
		}
		assertTrue {
			GenericHistoryDaoImpl.isNumber(
					GenericHistoryDaoImpl.getPropertyType(HostDynamic::memFree.name, HostDynamic::class)!!
			)
		}
		assertFalse {
			GenericHistoryDaoImpl.isNumber(
					GenericHistoryDaoImpl.getPropertyType(HostDynamic::status.name, HostDynamic::class)!!
			)
		}

		assertTrue { GenericHistoryDaoImpl.isNumber(Double::class.java) }
		assertTrue { GenericHistoryDaoImpl.isNumber(Int::class.java) }
		assertTrue { GenericHistoryDaoImpl.isNumber(Byte::class.java) }
		assertTrue { GenericHistoryDaoImpl.isNumber(BigDecimal::class.java) }
		assertTrue { GenericHistoryDaoImpl.isNumber(BigInteger::class.java) }

		assertFalse { GenericHistoryDaoImpl.isNumber(String::class.java) }
		assertFalse { GenericHistoryDaoImpl.isNumber(HostDynamic::class.java) }
	}

	@Test
	fun isData() {
		assertTrue { GenericHistoryDaoImpl.isData(HostDynamic::class.java) }
		assertFalse { GenericHistoryDaoImpl.isData(Boolean::class.java) }
		assertFalse { GenericHistoryDaoImpl.isData(BigDecimal::class.java) }
		assertFalse { GenericHistoryDaoImpl.isData(List::class.java) }
	}

	@Test
	fun detectExtremes() {
		val random = Random()
		//for each second
		val changes = (0..24 * 60 * 60).map {
			(it * 1000).toLong() to
					//extremes : 500 to 600, values 40 to 90
					if(it in 500..600) {
						PropertyChange(
								property = "workload",
								oldValue = 0,
								newValue = 40 + random.nextInt(50)
						)
					} else {
						//normal workload: below 5
						PropertyChange(
								property = "workload",
								oldValue = 0,
								newValue = random.nextInt(5)
						)
					}
		}
		val extremes = GenericHistoryDaoImpl.detectExtremes(changes)
		assertFalse (extremes.isEmpty())
	}
}