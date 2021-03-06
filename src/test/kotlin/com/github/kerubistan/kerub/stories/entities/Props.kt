package com.github.kerubistan.kerub.stories.entities

import cucumber.api.DataTable
import io.github.kerubistan.kroki.collections.skip

/**
 * This is only used to create test entities with tables,
 * where the first column is the property name and the second is the value
 */
fun <T> DataTable.forEachPair(action: (propName: String, propValue: String) -> T) =
		this.gherkinRows.skip().forEach {
			row ->
			action(row.cells[0], row.cells[1])
		}
