package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.block

import com.github.kerubistan.kerub.model.CompressionFormat
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.base.AbstractUnAllocate
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLv
import com.github.kerubistan.kerub.planner.steps.storage.lvm.unallocate.UnAllocateLv
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testLvmCapability
import com.github.kerubistan.kerub.testOtherHost
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID
import kotlin.test.assertTrue

class MigrateBlockAllocationTest {

	@Test
	fun validations() {
		assertThrows<IllegalStateException>("target and source hosts are the same") {
			val allocation = VirtualStorageLvmAllocation(
					actualSize = testDisk.size,
					capabilityId = testLvmCapability.id,
					hostId = testHost.id,
					path = "",
					vgName = testLvmCapability.volumeGroupName
			)
			MigrateBlockAllocation(
					sourceHost = testHost,
					targetHost = testHost,
					compression = null,
					allocationStep = CreateLv(
							host = testHost,
							disk = testDisk,
							capability = testLvmCapability
					),
					deAllocationStep = UnAllocateLv(
							host = testHost,
							allocation = allocation,
							vstorage = testDisk
					),
					sourceAllocation = allocation,
					virtualStorage = testDisk
			)
		}
		assertThrows<IllegalStateException>("compression not registered in the source host") {
			val allocation = VirtualStorageLvmAllocation(
					actualSize = testDisk.size,
					capabilityId = testLvmCapability.id,
					hostId = testHost.id,
					path = "",
					vgName = testLvmCapability.volumeGroupName
			)
			MigrateBlockAllocation(
					sourceHost = testHost,
					targetHost = testOtherHost,
					compression = CompressionFormat.Lz4,
					allocationStep = CreateLv(
							host = testHost,
							disk = testDisk,
							capability = testLvmCapability
					),
					deAllocationStep = UnAllocateLv(
							host = testHost,
							allocation = allocation,
							vstorage = testDisk
					),
					sourceAllocation = allocation,
					virtualStorage = testDisk
			)
		}
		assertThrows<IllegalStateException>("compression not registered in the target host") {
			val sourceHost = testHost.copy(
					capabilities = testHostCapabilities.copy(
							installedSoftware = listOf(
									SoftwarePackage("lz4", Version.fromVersionString("1.2.3"))
							)
					)
			)
			val targetHost = testOtherHost.copy(
					capabilities = testHostCapabilities.copy(
							installedSoftware = listOf(
									SoftwarePackage("gzip", Version.fromVersionString("1.2.3"))
							)
					)
			)
			val allocation = VirtualStorageLvmAllocation(
					actualSize = testDisk.size,
					capabilityId = testLvmCapability.id,
					hostId = sourceHost.id,
					path = "",
					vgName = testLvmCapability.volumeGroupName
			)
			MigrateBlockAllocation(
					sourceHost = sourceHost,
					targetHost = targetHost,
					compression = CompressionFormat.Lz4,
					allocationStep = CreateLv(
							host = testOtherHost,
							disk = testDisk,
							capability = testLvmCapability
					),
					deAllocationStep = UnAllocateLv(
							host = sourceHost,
							allocation = allocation,
							vstorage = testDisk
					),
					sourceAllocation = allocation,
					virtualStorage = testDisk
			)
		}
		assertThrows<IllegalStateException>("de-allocation must happen on ths source") {
			val sourceHost = testHost.copy(
					capabilities = testHostCapabilities.copy(
					)
			)
			val targetHost = testOtherHost.copy(
					capabilities = testHostCapabilities.copy(
					)
			)
			val allocation = VirtualStorageLvmAllocation(
					actualSize = testDisk.size,
					capabilityId = testLvmCapability.id,
					hostId = sourceHost.id,
					path = "",
					vgName = testLvmCapability.volumeGroupName
			)
			MigrateBlockAllocation(
					sourceHost = sourceHost.copy(id = randomUUID()),
					targetHost = targetHost,
					allocationStep = CreateLv(
							host = testOtherHost,
							disk = testDisk,
							capability = testLvmCapability
					),
					deAllocationStep = UnAllocateLv(
							host = sourceHost,
							allocation = allocation,
							vstorage = testDisk
					),
					sourceAllocation = allocation,
					virtualStorage = testDisk
			)
		}
		assertThrows<IllegalStateException>("allocation must happen on the target") {
			val sourceHost = testHost.copy(
					capabilities = testHostCapabilities
			)
			val targetHost = testOtherHost.copy(
					capabilities = testHostCapabilities
			)
			val allocation = VirtualStorageLvmAllocation(
					actualSize = testDisk.size,
					capabilityId = testLvmCapability.id,
					hostId = sourceHost.id,
					path = "",
					vgName = testLvmCapability.volumeGroupName
			)
			MigrateBlockAllocation(
					sourceHost = sourceHost,
					targetHost = targetHost.copy(id = randomUUID()),
					allocationStep = CreateLv(
							host = testOtherHost,
							disk = testDisk,
							capability = testLvmCapability
					),
					deAllocationStep = UnAllocateLv(
							host = sourceHost,
							allocation = allocation,
							vstorage = testDisk
					),
					sourceAllocation = allocation,
					virtualStorage = testDisk
			)
		}
	}

	@Test
	fun take() {
		// this is quite a bit dependent on the allocation and de-allocation steps, the implementation
		// only concatenates them, so let's try just with some mocking
		assertTrue("first allocate, then deallocate") {
			val sourceHost = testHost.copy(
					capabilities = testHostCapabilities.copy(
							installedSoftware = listOf(
									SoftwarePackage("lz4", Version.fromVersionString("1.2.3"))
							)
					)
			)
			val targetHost = testOtherHost.copy(
					capabilities = testHostCapabilities.copy(
							installedSoftware = listOf(
									SoftwarePackage("gzip", Version.fromVersionString("1.2.3"))
							)
					)
			)
			val allocation = VirtualStorageLvmAllocation(
					actualSize = testDisk.size,
					capabilityId = testLvmCapability.id,
					hostId = sourceHost.id,
					path = "",
					vgName = testLvmCapability.volumeGroupName
			)
			val allocationStep =
					mock<AbstractCreateVirtualStorage<out VirtualStorageBlockDeviceAllocation, out StorageCapability>>()
			whenever(allocationStep.host).thenReturn(targetHost)
			whenever(allocationStep.format).thenReturn(VirtualDiskFormat.raw)
			val deAllocationStep = mock<AbstractUnAllocate<*>>()
			whenever(deAllocationStep.host).thenReturn(sourceHost)
			val initialState = OperationalState.fromLists()
			val allocatedState = OperationalState.fromLists(
					hosts = listOf(
							testHost.copy(id = randomUUID())))
			val movedState = OperationalState.fromLists(
					hosts = listOf(
							testHost.copy(id = randomUUID())))
			whenever(allocationStep.take(eq(initialState))).thenReturn(allocatedState)
			whenever(deAllocationStep.take(eq(allocatedState))).thenReturn(movedState)
			MigrateBlockAllocation(
					sourceHost = sourceHost,
					targetHost = targetHost,
					allocationStep = allocationStep,
					deAllocationStep = deAllocationStep,
					sourceAllocation = allocation,
					virtualStorage = testDisk
			).take(initialState) == movedState

		}
	}
}