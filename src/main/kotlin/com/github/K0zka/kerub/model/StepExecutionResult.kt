package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(StepExecutionPass::class),
		JsonSubTypes.Type(StepExecutionSkip::class),
		JsonSubTypes.Type(StepExecutionError::class)
		)
interface StepExecutionResult {
	val executionStep : ExecutionStep
}