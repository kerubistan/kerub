package com.github.kerubistan.kerub.data.alerts

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Expectation
import java.util.UUID

@JsonTypeName("unsatisfied-expectation-alert")
data class UnsatisfiedExpectationAlert(
		override val id: UUID,
		override val created: Long,
		override val resolved: Long?,
		override val open: Boolean,
		val entityId : UUID,
		val expectation: Expectation
) : VirtualResourceAlert {
	init {
		this.validate()
	}
}