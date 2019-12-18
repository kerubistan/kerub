package com.github.kerubistan.kerub.planner.issues.problems.storage

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.StoragePoolDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.common.AbstractProblemDetectorVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testLvmCapability
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import kotlin.test.assertTrue

class LvmPoolAlmostFullDetectorTest : AbstractProblemDetectorVerifications(LvmPoolAlmostFullDetector) {

	@Test
	fun detect() {
		assertTrue("99% full but no overallocation at all, it fits nicely - no problem") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									testLvmCapability
							)
					)
			)
			LvmPoolAlmostFullDetector.detect(
					Plan(
							OperationalState.fromLists(
									hosts = listOf(
											host
									),
									hostCfgs = listOf(
											HostConfiguration(
													id = host.id,
													storageConfiguration = listOf(
															LvmPoolConfiguration(
																	poolName = "pool-1",
																	size = 500.GB,
																	vgName = testLvmCapability.volumeGroupName
															)
													)
											)
									),
									hostDyns = listOf(
											hostUp(host).copy(
													storageStatus = listOf(
															CompositeStorageDeviceDynamic(
																	id = testLvmCapability.id,
																	pools = listOf(
																			StoragePoolDynamic(
																					name = "pool-1",
																					size = 500.GB,
																					freeSize = 1.GB
																			)
																	)
															)
													)
											)
									)
							)
					)
			).isEmpty()
		}

		assertTrue("99% full but no overallocation, 110% allocation - detect problem") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									testLvmCapability
							)
					)
			)
			val disk = testDisk.copy(
					size = 4.TB,
					name = "large disk"
			)
			LvmPoolAlmostFullDetector.detect(
					Plan(
							OperationalState.fromLists(
									hosts = listOf(
											host
									),
									hostCfgs = listOf(
											HostConfiguration(
													id = host.id,
													storageConfiguration = listOf(
															LvmPoolConfiguration(
																	poolName = "pool-1",
																	size = 500.GB,
																	vgName = testLvmCapability.volumeGroupName
															)
													)
											)
									),
									hostDyns = listOf(
											hostUp(host).copy(
													storageStatus = listOf(
															CompositeStorageDeviceDynamic(
																	id = testLvmCapability.id,
																	pools = listOf(
																			StoragePoolDynamic(
																					name = "pool-1",
																					size = 500.GB,
																					freeSize = 1.GB
																			)
																	)
															)
													)
											)
									),
									vStorage = listOf(disk),
									vStorageDyns = listOf(
											VirtualStorageDeviceDynamic(
													id = disk.id,
													allocations = listOf(
															VirtualStorageLvmAllocation(
																	capabilityId = testLvmCapability.id,
																	actualSize = 499.GB,
																	hostId = host.id,
																	vgName = testLvmCapability.volumeGroupName,
																	path = "",
																	pool = "pool-1"
															)
													)
											)
									)
							)
					)
			).isNotEmpty()
		}

	}
}