package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.InternalExpectation
import com.github.K0zka.kerub.model.io.VirtualDiskFormat

/**
 * An expectation that is created when the virtual storage needs to be
 * available by the controller. It is internal since from user perspective
 * the storage should always be available.
 */
@JsonTypeName("storage-availability")
data class StorageAvailabilityExpectation(
		override val level: ExpectationLevel = ExpectationLevel.DealBreaker,
		val format: VirtualDiskFormat = VirtualDiskFormat.raw
) : InternalExpectation, VirtualStorageExpectation
