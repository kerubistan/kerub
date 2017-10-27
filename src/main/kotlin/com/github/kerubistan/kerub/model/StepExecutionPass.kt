package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("pass")
data class StepExecutionPass(
		override val executionStep: ExecutionStep,
		val timestamp: Long = System.currentTimeMillis()
) : StepExecutionResult