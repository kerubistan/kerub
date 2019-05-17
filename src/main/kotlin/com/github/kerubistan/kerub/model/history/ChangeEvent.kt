package com.github.kerubistan.kerub.model.history

import io.github.kerubistan.kroki.time.now
import java.io.Serializable
import java.util.UUID

data class ChangeEvent(
		override val id: UUID = UUID.randomUUID(),
		override val entityKey: Serializable,
		val time: Long = now(),
		override val appVersion: String?,
		val changes: List<PropertyChange>
) : Serializable, HistoryEntry