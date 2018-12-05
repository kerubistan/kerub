package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.pool.remove

import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.toSize
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class RemoveLvmPoolTest {
	@Test
	fun take() {
		assertTrue("") {
			val otherPool = LvmPoolConfiguration(
					poolName = "other-pool",
					size = "128 GB".toSize(),
					vgName = "test-vg"
			)
			val vgId = UUID.randomUUID()
			val host = testHost.copy(capabilities = testHostCapabilities.copy(
					storageCapabilities = listOf(
							LvmStorageCapability(
									id = vgId,
									size = "1 TB".toSize(),
									volumeGroupName = "test-vg",
									physicalVolumes = listOf("1 TB".toSize())
							)
					)
			))
			val newState = RemoveLvmPool(pool = "test-pool", vgName = "test-vg", host = host).take(
					OperationalState.fromLists(
							hosts = listOf(
									host
							),
							hostDyns = listOf(
									HostDynamic(
											id = host.id,
											status = HostStatus.Up,
											storageStatus = listOf(
													SimpleStorageDeviceDynamic(
															id = vgId,
															freeCapacity = "20 GB".toSize()
													)
											)
									)
							),
							hostCfgs = listOf(
									HostConfiguration(
											id = host.id,
											storageConfiguration = listOf(
													LvmPoolConfiguration(
															poolName = "test-pool",
															size = "128 GB".toSize(),
															vgName = "test-vg"
													),
													otherPool
											)
									)
							)
					)
			)

			newState.hosts[host.id]!!.config!!.storageConfiguration == listOf(otherPool)
		}
	}

}