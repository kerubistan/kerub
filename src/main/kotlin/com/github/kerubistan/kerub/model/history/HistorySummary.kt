package com.github.kerubistan.kerub.model.history

import com.github.kerubistan.kerub.model.Range
import org.hibernate.search.annotations.Field
import java.io.Serializable
import java.util.UUID

data class HistorySummary(
		override val id: UUID = UUID.randomUUID(),
		override val entityKey: Any,
		override val appVersion: String?,
		val time: Range<Long>,
		val changes: List<PropertyChangeSummary>
) : HistoryEntry, Serializable {
	val start : Long
		@Field
		get() = time.min
	val end : Long
		@Field
		get() = time.max
}