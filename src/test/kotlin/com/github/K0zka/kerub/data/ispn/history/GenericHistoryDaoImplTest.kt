package com.github.K0zka.kerub.data.ispn.history

import com.github.K0zka.kerub.model.Range
import com.github.K0zka.kerub.model.history.ChangeEvent
import com.github.K0zka.kerub.model.history.HistorySummary
import com.github.K0zka.kerub.model.history.PropertyChange
import com.github.K0zka.kerub.model.history.PropertyChangeSummary
import com.github.K0zka.kerub.testHost
import org.junit.Test
import kotlin.test.assertEquals
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
										PropertyChangeSummary(
												property = "foo",
												min = 1,
												max = 5,
												average = 3,
												extremes = listOf()
										)
								),
								time = Range(10, 1000),
								entityKey = testHost.id
						),
						HistorySummary(
								appVersion = "",
								changes = listOf(
										PropertyChangeSummary(
												property = "bar",
												min = 1,
												max = 5,
												average = 3,
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
										PropertyChangeSummary(
												property = "foo",
												min = 1,
												max = 5,
												average = 3,
												extremes = listOf()
										)
								),
								time = Range(10, 1000),
								entityKey = testHost.id
						),
						HistorySummary(
								appVersion = "",
								changes = listOf(
										PropertyChangeSummary(
												property = "bar",
												min = 1,
												max = 5,
												average = 3,
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
}