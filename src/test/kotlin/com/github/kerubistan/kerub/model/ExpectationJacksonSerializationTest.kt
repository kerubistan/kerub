package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.model.expectations.BandwidthExpectation
import com.github.kerubistan.kerub.model.expectations.CacheSizeExpectation
import com.github.kerubistan.kerub.model.expectations.ChassisManufacturerExpectation
import com.github.kerubistan.kerub.model.expectations.ClockFrequencyExpectation
import com.github.kerubistan.kerub.model.expectations.CoreDedicationExpectation
import com.github.kerubistan.kerub.model.expectations.EccMemoryExpectation
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
import com.github.kerubistan.kerub.model.expectations.StorageReadWritePerformance
import com.github.kerubistan.kerub.model.expectations.StorageRedundancyExpectation
import com.github.kerubistan.kerub.model.expectations.StorageWritePerformanceExpectation
import com.github.kerubistan.kerub.model.expectations.SystemManufacturerExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.model.io.IoTune
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.model.site.SiteFeature
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.createObjectMapper
import com.github.kerubistan.kerub.utils.getLogger
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.KB
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ExpectationJacksonSerializationTest {

	@Parameterized.Parameter
	lateinit var parameter: Any

	companion object {
		private val logger = getLogger()
		@JvmStatic
		@Parameterized.Parameters
		fun parameters() = listOf(
				BandwidthExpectation(bandwidthBitPerSecond = 10.GB.toLong()),
				CacheSizeExpectation(minL1 = 512.KB.toLong(), level = ExpectationLevel.Want),
				LatencyExpectation(latencyMicroSec = 500),
				NetworkRedundancyExpectation(minRedundancy = 3, level = ExpectationLevel.Want),
				ChassisManufacturerExpectation(manufacturer = "Sallala-Hallala International"),
				ClockFrequencyExpectation(minimalClockFrequency = 2000, level = ExpectationLevel.Want),
				CoreDedicationExpectation(),
				MemoryClockFrequencyExpectation(min = 1600, level = ExpectationLevel.Want),
				NotSameHostExpectation(otherVmId = testVm.id),
				NoMigrationExpectation(userTimeoutMinutes = 60),
				NotSameStorageExpectation(otherDiskId = testDisk.id),
				PoolAllVmExpectation(vmExpectation = EccMemoryExpectation()),
				PoolAverageLoadExpectation(max = 90, min = 50, toleranceMs = 5000),
				PoolRunningVmsExpectation(min = 2, max = 8),
				PowerRedundancyExpectation(minPowerCords = 2),
				SiteFeaturesExpectation(
						features = listOf(SiteFeature.RenewablePower, SiteFeature.UPS),
						level = ExpectationLevel.DealBreaker),
				StorageAvailabilityExpectation(format = VirtualDiskFormat.qcow2),
				StorageReadWritePerformance(
						speed = IoTune(kbPerSec = 100, iopsPerSec = 10000),
						level = ExpectationLevel.Want),
				StorageRedundancyExpectation(nrOfCopies = 3, outOfBox = true),
				StorageWritePerformanceExpectation(
						speed = IoTune(kbPerSec = 100, iopsPerSec = 10000),
						level = ExpectationLevel.Want),
				SystemManufacturerExpectation(manufacturer = "Lofsz Corporation"),
				VirtualMachineAvailabilityExpectation()
		).map { arrayOf(it) }
	}

	@Test
	fun jacksonSerialization() {
		val mapper = createObjectMapper()
		val serialized = mapper.writeValueAsString(parameter)
		logger.info(serialized)
		val deserialized = mapper.readValue(serialized, Expectation::class.java)
		assertEquals(parameter, deserialized)
	}
}