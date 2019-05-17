package com.github.kerubistan.kerub.model

import io.github.kerubistan.kroki.time.now
import org.hibernate.search.annotations.Indexed
import java.util.UUID

@Indexed
data class ExecutionResult(
		override val id: UUID = UUID.randomUUID(),
		val timestamp: Long = now(),
		val started: Long,
		val controllerId: String,
		val steps: List<StepExecutionResult>
) : Entity<UUID>