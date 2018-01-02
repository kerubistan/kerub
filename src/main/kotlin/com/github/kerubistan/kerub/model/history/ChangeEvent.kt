package com.github.kerubistan.kerub.model.history

import com.github.kerubistan.kerub.utils.now
import java.io.Serializable
import java.util.UUID

data class ChangeEvent(
		override val id: UUID = UUID.randomUUID(),
		override val entityKey: Any,
		val time: Long = now(),
		override val appVersion: String?,
		val changes: List<PropertyChange>
) : Serializable, HistoryEntry