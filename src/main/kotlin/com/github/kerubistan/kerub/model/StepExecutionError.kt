package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.utils.now

@JsonTypeName("err")
data class StepExecutionError(
		override val executionStep: ExecutionStep,
		val timestamp: Long = now(),
		val error: String
) : StepExecutionResult