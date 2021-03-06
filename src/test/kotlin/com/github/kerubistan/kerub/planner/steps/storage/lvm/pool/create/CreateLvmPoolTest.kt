package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.create

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
import com.github.kerubistan.kerub.testOtherHost
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CreateLvmPoolTest : OperationalStepVerifications() {

	@Test
	fun validations() {
		assertThrows<IllegalStateException>("Pool bigger than the VG") {
			val capability = LvmStorageCapability(
					id = randomUUID(),
					size = 8.TB,
					volumeGroupName = "test-vg",
					physicalVolumes = mapOf("/dev/sda" to 4.TB, "/dev/sdb" to 4.TB)
			)
			CreateLvmPool(
					host = testHost.copy(
							capabilities = testHostCapabilities.copy(
									storageCapabilities = listOf(
											capability
									)
							)
					),
					size = 10.TB,
					vgName = capability.volumeGroupName,
					name = randomUUID().toString()
			)
		}
	}

	@Test
	fun isLikeStep() {
		val step = CreateLvmPool(
				host = testHost.copy(
						capabilities = testHostCapabilities.copy(
								storageCapabilities = listOf(
										LvmStorageCapability(
												id = randomUUID(),
												size = 8.TB,
												volumeGroupName = "test-vg",
												physicalVolumes = mapOf("/dev/sda" to 4.TB, "/dev/sdb" to 4.TB)
										)
								)
						)
				), size = 2.TB, vgName = "test-vg", name = "pool-1")
		val otherStep = CreateLvmPool(
				host = testOtherHost.copy(
						capabilities = testHostCapabilities.copy(
								storageCapabilities = listOf(
										LvmStorageCapability(
												id = randomUUID(),
												size = 8.TB,
												volumeGroupName = "test-vg",
												physicalVolumes = mapOf("/dev/sda" to 4.TB, "/dev/sdb" to 4.TB)
										)
								)
						)
				), size = 2.TB, vgName = "test-vg", name = "pool-1")
		assertTrue(step.isLikeStep(step))
		assertFalse(step.isLikeStep(otherStep))
	}

	override val step: AbstractOperationalStep
		get() = CreateLvmPool(
				host = testHost.copy(
						capabilities = testHostCapabilities.copy(
								storageCapabilities = listOf(
										LvmStorageCapability(
												id = randomUUID(),
												size = 8.TB,
												volumeGroupName = "test-vg",
												physicalVolumes = mapOf("/dev/sda" to 4.TB, "/dev/sdb" to 4.TB)
										)
								)
						)
				), size = 2.TB, vgName = "test-vg", name = "pool-1")

	@Test
	fun take() {
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(
								LvmStorageCapability(
										id = randomUUID(),
										size = 8.TB,
										volumeGroupName = "test-vg",
										physicalVolumes = mapOf("/dev/sda" to 4.TB, "/dev/sdb" to 4.TB)
								)
						)
				)
		)
		val state = CreateLvmPool(host = host, size = 2.TB, vgName = "test-vg", name = "pool-1").take(
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
			val poolConfig = (state.hosts.getValue(testHost.id).config!!.storageConfiguration.single() as LvmPoolConfiguration)
			poolConfig.poolName == "pool-1"
					&& poolConfig.size == 2.TB
					&& poolConfig.vgName == "test-vg"
		}
	}
}