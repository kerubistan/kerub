package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

/**
 * Tracks a violation of the SLA.
 */
@JsonTypeName("violation")
data class Violation(
		override val id: UUID = UUID.randomUUID(),
		val entity: Constrained<out Expectation>,
		val violatedConstraint: Expectation,
		val start: Long,
		val end: Long? = null
) : Entity<UUID> {
	init {
		check(violatedConstraint in entity.expectations) { "violated constraint not part of the expectations" }
		if (end != null) {
			check(end >= start) { "start should not after the end" }
		}
	}
}