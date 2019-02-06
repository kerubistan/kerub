package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.remove

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class RemoveLvmPoolTest : OperationalStepVerifications() {
	override val step: AbstractOperationalStep
		get() = RemoveLvmPool(
				pool = "test-pool", vgName = "test-vg", host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(
								LvmStorageCapability(
										id = UUID.randomUUID(),
										size = 1.TB,
										volumeGroupName = "test-vg",
										physicalVolumes = mapOf("/dev/sda" to 1.TB)
								)
						)
				)))

	@Test
	fun take() {
		assertTrue("") {
			val otherPool = LvmPoolConfiguration(
					poolName = "other-pool",
					size = 128.GB,
					vgName = "test-vg"
			)
			val vgId = UUID.randomUUID()
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									LvmStorageCapability(
											id = vgId,
											size = 1.TB,
											volumeGroupName = "test-vg",
											physicalVolumes = mapOf("/dev/sda" to 1.TB)
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
															freeCapacity = 20.GB
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
															size = 128.GB,
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