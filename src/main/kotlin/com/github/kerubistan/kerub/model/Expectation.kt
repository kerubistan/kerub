package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.kerubistan.kerub.model.expectations.BandwidthExpectation
import com.github.kerubistan.kerub.model.expectations.CacheSizeExpectation
import com.github.kerubistan.kerub.model.expectations.ChassisManufacturerExpectation
import com.github.kerubistan.kerub.model.expectations.ClockFrequencyExpectation
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.model.expectations.CoreDedicationExpectation
import com.github.kerubistan.kerub.model.expectations.CpuArchitectureExpectation
import com.github.kerubistan.kerub.model.expectations.EccMemoryExpectation
import com.github.kerubistan.kerub.model.expectations.HostOperatingSystemExpectation
import com.github.kerubistan.kerub.model.expectations.LatencyExpectation
import com.github.kerubistan.kerub.model.expectations.MemoryClockFrequencyExpectation
import com.github.kerubistan.kerub.model.expectations.NetworkRedundancyExpectation
import com.github.kerubistan.kerub.model.expectations.NoMigrationExpectation
import com.github.kerubistan.kerub.model.expectations.NotSameHostExpectation
import com.github.kerubistan.kerub.model.expectations.NotSameStorageExpectation
import com.github.kerubistan.kerub.model.expectations.PoolAllVmExpectation
import com.github.kerubistan.kerub.model.expectations.PoolAverageLoadExpectation
import com.github.kerubistan.kerub.model.expectations.PoolRunningVmsExpectation
import com.github.kerubistan.kerub.model.expectations.PowerRedundancyExpectation
import com.github.kerubistan.kerub.model.expectations.SiteFeaturesExpectation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.expectations.StorageReadPerformanceExpectation
import com.github.kerubistan.kerub.model.expectations.StorageReadWritePerformance
import com.github.kerubistan.kerub.model.expectations.StorageRedundancyExpectation
import com.github.kerubistan.kerub.model.expectations.StorageWritePerformanceExpectation
import com.github.kerubistan.kerub.model.expectations.SystemManufacturerExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.model.expectations.VmDependency
import java.io.Serializable

/**
 * Base expectation interface.
 * Expectations describe SLA on virtual resources.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(HostOperatingSystemExpectation::class),
		JsonSubTypes.Type(NoMigrationExpectation::class),
		JsonSubTypes.Type(VirtualMachineAvailabilityExpectation::class),
		JsonSubTypes.Type(EccMemoryExpectation::class),
		JsonSubTypes.Type(CoreDedicationExpectation::class),
		JsonSubTypes.Type(ChassisManufacturerExpectation::class),
		JsonSubTypes.Type(NotSameHostExpectation::class),
		JsonSubTypes.Type(PowerRedundancyExpectation::class),
		JsonSubTypes.Type(CpuArchitectureExpectation::class),
		JsonSubTypes.Type(ClockFrequencyExpectation::class),
		JsonSubTypes.Type(MemoryClockFrequencyExpectation::class),
		JsonSubTypes.Type(CacheSizeExpectation::class),
		JsonSubTypes.Type(SystemManufacturerExpectation::class),
		JsonSubTypes.Type(SiteFeaturesExpectation::class),
		JsonSubTypes.Type(VmDependency::class),
		// virtual storage expectations
		JsonSubTypes.Type(StorageAvailabilityExpectation::class),
		JsonSubTypes.Type(NotSameStorageExpectation::class),
		JsonSubTypes.Type(StorageRedundancyExpectation::class),
		JsonSubTypes.Type(StorageReadPerformanceExpectation::class),
		JsonSubTypes.Type(StorageReadWritePerformance::class),
		JsonSubTypes.Type(StorageWritePerformanceExpectation::class),
		JsonSubTypes.Type(CloneOfStorageExpectation::class),
		// pool expectations
		JsonSubTypes.Type(PoolAllVmExpectation::class),
		JsonSubTypes.Type(PoolAverageLoadExpectation::class),
		JsonSubTypes.Type(PoolRunningVmsExpectation::class),
		// virtual network
		JsonSubTypes.Type(BandwidthExpectation::class),
		JsonSubTypes.Type(LatencyExpectation::class),
		JsonSubTypes.Type(NetworkRedundancyExpectation::class)
)
interface Expectation : Serializable {
	val level: ExpectationLevel
}
