package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.extend

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class ExtendLvmPoolFactoryTest : AbstractFactoryVerifications(ExtendLvmPoolFactory) {

	@Test
	fun produce() {
		assertTrue("No volume groups even - no steps generated") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(BlockDevice(
									deviceName = "/dev/sdb",
									storageCapacity = 1.TB
							)),
							storageCapabilities = listOf()
					)
			)
			val hostDyn = hostUp(host)
			ExtendLvmPoolFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostDyn),
							hostCfgs = listOf(
									HostConfiguration(
											id = host.id
									)
							)
					)
			).isEmpty()
		}
		assertTrue("No LVM pools - no steps generated") {
			val lvmStorageCapability = LvmStorageCapability(
					id = UUID.randomUUID(),
					size = 1.TB,
					physicalVolumes = mapOf("/dev/sdb" to 1.TB),
					volumeGroupName = "vg1"
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(BlockDevice(
									deviceName = "/dev/sdb",
									storageCapacity = 1.TB
							)),
							storageCapabilities = listOf(
									lvmStorageCapability
							)
					)
			)
			val hostDyn = hostUp(host).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = lvmStorageCapability.id,
									freeCapacity = 0.GB
							)
					)
			)
			ExtendLvmPoolFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostDyn),
							hostCfgs = listOf(
									HostConfiguration(
											id = host.id
									)
							)
					)
			).isEmpty()
		}
		assertTrue("LVM pool exists, but it already takes all space - no steps should offered") {
			val lvmStorageCapability = LvmStorageCapability(
					id = UUID.randomUUID(),
					size = 1.TB,
					physicalVolumes = mapOf("/dev/sdb" to 1.TB),
					volumeGroupName = "vg1"
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(BlockDevice(
									deviceName = "/dev/sdb",
									storageCapacity = 1.TB
							)),
							storageCapabilities = listOf(
									lvmStorageCapability
							)
					)
			)
			val hostDyn = hostUp(host).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = lvmStorageCapability.id,
									freeCapacity = 0.GB
							)
					)
			)
			ExtendLvmPoolFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostDyn),
							hostCfgs = listOf(
									HostConfiguration(
											id = host.id,
											storageConfiguration = listOf(
													LvmPoolConfiguration(
															size = 1.TB,
															vgName = lvmStorageCapability.volumeGroupName,
															poolName = "test-thin-pool"
													)
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("LVM pool exists, but no free space - no steps should offered") {
			val lvmStorageCapability = LvmStorageCapability(
					id = UUID.randomUUID(),
					size = 1.TB,
					physicalVolumes = mapOf("/dev/sdb" to 1.TB),
					volumeGroupName = "vg1"
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(BlockDevice(
									deviceName = "/dev/sdb",
									storageCapacity = 1.TB
							)),
							storageCapabilities = listOf(
									lvmStorageCapability
							)
					)
			)
			val hostDyn = hostUp(host).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = lvmStorageCapability.id,
									freeCapacity = 0.GB
							)
					)
			)
			ExtendLvmPoolFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostDyn),
							hostCfgs = listOf(
									HostConfiguration(
											id = host.id,
											storageConfiguration = listOf(
													LvmPoolConfiguration(
															size = 500.GB,
															vgName = lvmStorageCapability.volumeGroupName,
															poolName = "test-thin-pool"
													)
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("LVM pool exists, there is free space - get an offer") {
			val lvmStorageCapability = LvmStorageCapability(
					id = UUID.randomUUID(),
					size = 1.TB,
					physicalVolumes = mapOf("/dev/sdb" to 1.TB),
					volumeGroupName = "vg1"
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(BlockDevice(
									deviceName = "/dev/sdb",
									storageCapacity = 1.TB
							)),
							storageCapabilities = listOf(
									lvmStorageCapability
							)
					)
			)
			val hostDyn = hostUp(host).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = lvmStorageCapability.id,
									freeCapacity = 500.GB
							)
					)
			)
			ExtendLvmPoolFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostDyn),
							hostCfgs = listOf(
									HostConfiguration(
											id = host.id,
											storageConfiguration = listOf(
													LvmPoolConfiguration(
															size = 500.GB,
															vgName = lvmStorageCapability.volumeGroupName,
															poolName = "test-thin-pool"
													)
											)
									)
							)
					)
			).let {
				it.isNotEmpty()
					&&  it.all {
					it.host == host
						&& it.pool == "test-thin-pool"
						&& it.vgName == lvmStorageCapability.volumeGroupName
				}
			}
		}
	}
}