package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.utils

import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testLvmCapability
import com.github.kerubistan.kerub.utils.genPassword
import org.junit.Test
import kotlin.test.assertEquals

class IscsiUtilsKtTest {

	@Test
	fun iscsiShareableDisks() {
		val allocation = VirtualStorageLvmAllocation(
				hostId = testHost.id,
				actualSize = testDisk.size,
				path = "/dev/test/1234",
				vgName = "test",
				capabilityId = testLvmCapability.id
		)
		val diskDyn = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocations = listOf(allocation)
		)
		assertEquals(
				mapOf(
						VirtualStorageDataCollection(stat = testDisk, dynamic = diskDyn) to listOf(allocation)
				),
				iscsiShareableDisks(
						OperationalState.fromLists(
								vStorage = listOf(testDisk),
								vStorageDyns = listOf(diskDyn),
								hosts = listOf(testHost),
								hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up))
						)
				)
		)
	}

	@Test
	fun iscsiShareableDisksWithSharedDisk() {
		val allocation = VirtualStorageLvmAllocation(
				hostId = testHost.id,
				actualSize = testDisk.size,
				path = "/dev/test/1234",
				vgName = "test",
				capabilityId = testLvmCapability.id
		)
		val diskDyn = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocations = listOf(allocation)
		)
		val hostDyn = HostDynamic(
				id = testHost.id,
				status = HostStatus.Up
		)
		val hostConfig = HostConfiguration(
				id = testHost.id,
				services = listOf(
						IscsiService(
								password = genPassword(),
								vstorageId = testDisk.id
						)
				)
		)
		assertEquals(
				mapOf(),
				iscsiShareableDisks(
						OperationalState.fromLists(
								vStorage = listOf(testDisk),
								vStorageDyns = listOf(diskDyn),
								hosts = listOf(testHost),
								hostCfgs = listOf(hostConfig),
								hostDyns = listOf(hostDyn)
						)
				)
		)
	}

	@Test
	fun iscsiSharedDisks() {
		val allocation = VirtualStorageLvmAllocation(
				hostId = testHost.id,
				actualSize = testDisk.size,
				path = "/dev/test/1234",
				vgName = "test",
				capabilityId = testLvmCapability.id
		)
		val diskDyn = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocations = listOf(allocation)
		)
		val hostDyn = HostDynamic(
				id = testHost.id,
				status = HostStatus.Up
		)
		val hostConfig = HostConfiguration(
				id = testHost.id,
				services = listOf(
						IscsiService(
								password = genPassword(),
								vstorageId = testDisk.id
						)
				)
		)
		assertEquals(
				mapOf(
						VirtualStorageDataCollection(
								stat = testDisk,
								dynamic = diskDyn
						) to listOf(allocation)
				),
				iscsiSharedDisks(
						OperationalState.fromLists(
								vStorage = listOf(testDisk),
								vStorageDyns = listOf(diskDyn),
								hosts = listOf(testHost),
								hostCfgs = listOf(hostConfig),
								hostDyns = listOf(hostDyn)
						)
				)
		)

	}

}