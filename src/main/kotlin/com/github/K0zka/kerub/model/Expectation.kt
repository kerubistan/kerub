package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.K0zka.kerub.model.expectations.CacheSizeExpectation
import com.github.K0zka.kerub.model.expectations.ChassisManufacturerExpectation
import com.github.K0zka.kerub.model.expectations.ClockFrequencyExpectation
import com.github.K0zka.kerub.model.expectations.CpuArchitectureExpectation
import com.github.K0zka.kerub.model.expectations.CpuDedicationExpectation
import com.github.K0zka.kerub.model.expectations.EccMemoryExpectation
import com.github.K0zka.kerub.model.expectations.HostOperatingSystemExpectation
import com.github.K0zka.kerub.model.expectations.MemoryClockFrequencyExpectation
import com.github.K0zka.kerub.model.expectations.NoMigrationExpectation
import com.github.K0zka.kerub.model.expectations.NotSameHostExpectation
import com.github.K0zka.kerub.model.expectations.NotSameStorageExpectation
import com.github.K0zka.kerub.model.expectations.PowerRedundancyExpectation
import com.github.K0zka.kerub.model.expectations.SiteFeaturesExpectation
import com.github.K0zka.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.K0zka.kerub.model.expectations.StoragePerformanceExpectation
import com.github.K0zka.kerub.model.expectations.StorageReadPerformanceExpectation
import com.github.K0zka.kerub.model.expectations.StorageReadWritePerformance
import com.github.K0zka.kerub.model.expectations.StorageRedundancyExpectation
import com.github.K0zka.kerub.model.expectations.StorageWritePerformanceExpectation
import com.github.K0zka.kerub.model.expectations.SystemManufacturerExpectation
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.model.expectations.VmDependency
import java.io.Serializable

/**
 * Base expectation interface.
 * Expectations describe SLA on virtual resources.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
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
		JsonSubTypes.Type(SystemManufacturerExpectation::class),
		JsonSubTypes.Type(SiteFeaturesExpectation::class),
		JsonSubTypes.Type(StorageAvailabilityExpectation::class),
		JsonSubTypes.Type(VmDependency::class)
)
interface Expectation : Serializable {
	val level: ExpectationLevel
}
