package com.github.K0zka.kerub.model.history

import java.io.Serializable
import java.util.UUID

data class HistoryEntry(
		val id: UUID = UUID.randomUUID(),
		val entityKey: Any,
		val time: Long = System.currentTimeMillis(),
		val appVersion: String?,
		val changes: List<PropertyChange>
) : Serializable