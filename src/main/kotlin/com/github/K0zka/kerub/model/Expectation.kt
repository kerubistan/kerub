package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.K0zka.kerub.model.expectations.*
import java.io.Serializable
import java.util.UUID

/**
 * Base expectation interface.
 * Expectations describe SLA on virtual resources.
 */
JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
JsonSubTypes(
		JsonSubTypes.Type(HostOperatingSystemExpectation::class),
		JsonSubTypes.Type(NoMigrationExpectation::class),
		JsonSubTypes.Type(StoragePerformanceExpectation::class),
		JsonSubTypes.Type(StorageReadPerformanceExpectation::class),
		JsonSubTypes.Type(StorageReadWritePerformance::class),
		JsonSubTypes.Type(StorageWritePerformanceExpectation::class),
		JsonSubTypes.Type(VirtualMachineAvailabilityExpectation::class),
		JsonSubTypes.Type(EccMemoryExpectation::class),
		JsonSubTypes.Type(CpuDedicationExpectation::class),
		JsonSubTypes.Type(ChassisManufacturerExpectation::class),
		JsonSubTypes.Type(NotSameHostExpectation::class),
		JsonSubTypes.Type(PowerRedundancyExpectation::class),
		JsonSubTypes.Type(NotSameStorageExpectation::class),
		JsonSubTypes.Type(CpuArchitectureExpectation::class),
		JsonSubTypes.Type(StorageRedundancyExpectation::class),
		JsonSubTypes.Type(ClockFrequencyExpectation::class),
		JsonSubTypes.Type(MemoryClockFrequencyExpectation::class),
		JsonSubTypes.Type(CacheSizeExpectation::class),
		JsonSubTypes.Type(SystemManufacturerExpectation::class)
            )
data interface Expectation : Serializable {
	val level: ExpectationLevel
}
