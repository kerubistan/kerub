package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.expectations.VirtualMachineExpectation
import com.github.K0zka.kerub.model.expectations.VirtualStorageExpectation

/**
 * An expectation that is broken only when the vm/storage is hosted by
 * a host being removed or crashing
 */
@JsonTypeName("working-host")
data class WorkingHostExpectation(override val level: ExpectationLevel = ExpectationLevel.DealBreaker) :
		InternalExpectation,
		VirtualStorageExpectation,
		VirtualMachineExpectation