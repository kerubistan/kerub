package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.kerubistan.kroki.time.now

@JsonTypeName("pass")
data class StepExecutionPass(
		override val executionStep: ExecutionStep,
		val timestamp: Long = now()
) : StepExecutionResult