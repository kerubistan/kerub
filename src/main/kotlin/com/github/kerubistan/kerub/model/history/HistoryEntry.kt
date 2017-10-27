package com.github.kerubistan.kerub.model.history

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.search.annotations.Analyze
import org.hibernate.search.annotations.Field
import java.util.UUID

interface HistoryEntry {
	val id: UUID
	val entityKey: Any
	val entityKeyStr: String
		@Field(analyze = Analyze.NO)
		@JsonIgnore
		get() = entityKey.toString()
	val appVersion: String?
}