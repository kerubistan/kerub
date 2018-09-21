package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("skip")
data class StepExecutionSkip(override val executionStep: ExecutionStep) : StepExecutionResult