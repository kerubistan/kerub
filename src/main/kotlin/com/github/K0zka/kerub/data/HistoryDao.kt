package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.dynamic.DynamicEntity
import com.github.K0zka.kerub.model.history.HistoryEntry
import java.util.UUID

interface HistoryDao<in T : DynamicEntity> {
	fun log(oldEntry: T, newEntry: T)
	fun list(id: UUID): List<HistoryEntry>
	fun compress(from: Long, to: Long, entityIds: Collection<UUID>)
}