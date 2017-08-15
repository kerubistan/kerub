package com.github.K0zka.kerub.model.history

import java.util.UUID

interface HistoryEntry {
	val id: UUID
	val entityKey: Any
	val appVersion: String?
}