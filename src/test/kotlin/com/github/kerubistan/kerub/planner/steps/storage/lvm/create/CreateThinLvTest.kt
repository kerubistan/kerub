package com.github.kerubistan.kerub.planner.steps.storage.lvm.create

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertTrue

class CreateThinLvTest {
	@Test
	fun take() {
		assertTrue("") {
			val lvmCapability = LvmStorageCapability(
					volumeGroupName = "vg-1",
					physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB),
					size = 2.TB
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									lvmCapability
							)
					)
			)
			val hostCfg = HostConfiguration(
					storageConfiguration = listOf(
							LvmPoolConfiguration(poolName = "pool-1", size = 512.GB, vgName = "vg-1")
					)
			)
			val disk = testDisk.copy(
					size = 1.TB
			)
			val state = CreateThinLv(
					host = host,
					disk = disk,
					capability = lvmCapability,
					poolName = "pool-1").take(
					OperationalState.fromLists(
							vStorage = listOf(disk),
							hosts = listOf(host),
							hostCfgs = listOf(hostCfg)
					)
			)

			state.vStorage.getValue(disk.id).dynamic!!.allocations == listOf(
					VirtualStorageLvmAllocation(hostId = host.id,
							pool = "pool-1",
							actualSize = BigInteger.ZERO,
							path = "",
							vgName = "vg-1",
							capabilityId = lvmCapability.id
					)
			)
		}
	}

}