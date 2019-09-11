package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.extend

import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamicItem
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class ExtendLvmPoolTest : OperationalStepVerifications() {
	override val step: AbstractOperationalStep
		get() = ExtendLvmPool(vgName = "test-vg", pool = "test-pool", host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(
								LvmStorageCapability(
										id = UUID.randomUUID(),
										size = 2.TB,
										physicalVolumes = mapOf("/dev/sda" to 2.TB),
										volumeGroupName = "test-vg"
								)
						)
				)
		), addSize = 128.GB)

	@Test
	fun take() {
		val lvmStorageId = UUID.randomUUID()
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(
								LvmStorageCapability(
										id = lvmStorageId,
										size = 2.TB,
										physicalVolumes = mapOf("/dev/sda" to 2.TB),
										volumeGroupName = "test-vg"
								)
						)
				)
		)
		val original = OperationalState.fromLists(
				hosts = listOf(host),
				hostDyns = listOf(
					HostDynamic(
							id = testHost.id,
							status = HostStatus.Up,
							storageStatus = listOf(
									CompositeStorageDeviceDynamic(
											id = lvmStorageId,
											reportedFreeCapacity = 600.GB,
											items = listOf(
													CompositeStorageDeviceDynamicItem(
															name = "/dev/sda",
															freeCapacity = 600.GB
													)
											)
									)
							)
					)
				),
				hostCfgs = listOf(
						HostConfiguration(
								id = testHost.id,
								storageConfiguration = listOf(
										LvmPoolConfiguration(
												poolName = "test-pool",
												size = 256.GB,
												vgName = "test-vg"
										)
								)
						)
				)
		)
		val state = ExtendLvmPool(vgName = "test-vg", pool = "test-pool", host = host, addSize = 128.GB)
				.take(original)
		assertTrue {
			val configuration = state.hosts.getValue(host.id).config!!.storageConfiguration.single()
					as LvmPoolConfiguration
			configuration.poolName == "test-pool"
				&& configuration.size == 128.GB + 256.GB
				&& configuration.vgName == "test-vg"
		}
	}
}