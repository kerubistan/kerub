package com.github.K0zka.kerub.model

import org.hibernate.search.annotations.Indexed
import java.util.UUID

@Indexed
data class ExecutionResult(
		override val id: UUID = UUID.randomUUID(),
		val timestamp: Long = System.currentTimeMillis(),
		val started: Long,
		val controllerId: String,
		val steps: List<StepExecutionResult>
) : Entity<UUID>