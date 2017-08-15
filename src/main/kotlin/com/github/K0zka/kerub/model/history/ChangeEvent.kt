package com.github.K0zka.kerub.model.history

import java.io.Serializable
import java.util.UUID

data class ChangeEvent(
		override val id: UUID = UUID.randomUUID(),
		override val entityKey: Any,
		val time: Long = System.currentTimeMillis(),
		override val appVersion: String?,
		val changes: List<PropertyChange>
) : Serializable, HistoryEntry