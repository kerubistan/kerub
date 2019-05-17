package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.kerubistan.kroki.time.now

@JsonTypeName("err")
data class StepExecutionError(
		override val executionStep: ExecutionStep,
		val timestamp: Long = now(),
		val error: String
) : StepExecutionResult