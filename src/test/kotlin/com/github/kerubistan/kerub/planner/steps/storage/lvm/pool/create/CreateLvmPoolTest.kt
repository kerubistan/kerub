package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.create

import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.toSize
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class CreateLvmPoolTest : OperationalStepVerifications() {
	override val step: AbstractOperationalStep
		get() = CreateLvmPool(
				host = testHost.copy(
						capabilities = testHostCapabilities.copy(
								storageCapabilities = listOf(
										LvmStorageCapability(
												id = UUID.randomUUID(),
												size = 8.TB,
												volumeGroupName = "test-vg",
												physicalVolumes = mapOf("/dev/sda" to 4.TB, "/dev/sdb" to 4.TB)
										)
								)
						)
				), size = "2 TB".toSize(), vgName = "test-vg", name = "pool-1")

	@Test
	fun take() {
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(
								LvmStorageCapability(
										id = UUID.randomUUID(),
										size = 8.TB,
										volumeGroupName = "test-vg",
										physicalVolumes = mapOf("/dev/sda" to 4.TB, "/dev/sdb" to 4.TB)
								)
						)
				)
		)
		val state = CreateLvmPool(host = host, size = "2 TB".toSize(), vgName = "test-vg", name = "pool-1").take(
				OperationalState.fromLists(
						hosts = listOf(
								host
						),
						hostDyns = listOf(
								HostDynamic(
										id = host.id,
										status = HostStatus.Up
								)
						),
						hostCfgs = listOf(
								HostConfiguration(
										id = host.id,
										storageConfiguration = listOf()
								)
						)
				)
		)

		assertTrue {
			val poolConfig = (state.hosts[testHost.id]!!.config!!.storageConfiguration.single() as LvmPoolConfiguration)
			poolConfig.poolName == "pool-1"
					&& poolConfig.size == "2 TB".toSize()
					&& poolConfig.vgName == "test-vg"
		}
	}
}