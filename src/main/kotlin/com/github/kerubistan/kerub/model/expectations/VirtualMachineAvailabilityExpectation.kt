package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

@JsonTypeName("availability")
data class VirtualMachineAvailabilityExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.DealBreaker,
		/**
		 * If the VM needs to be available in desktop mode, it may be hibernated/suspended
		 * when the user is not connected to console.
		 */
		val desktop: Boolean = false,
		val up: Boolean = true
) : VirtualMachineExpectation