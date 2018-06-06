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
import com.github.kerubistan.kerub.utils.genPassword
import org.junit.Test

class IscsiUtilsKtTest {

	@Test
	fun unsharedDisks() {
		val diskDyn = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocations = listOf(VirtualStorageLvmAllocation(
						hostId = testHost.id,
						actualSize = testDisk.size,
						path = "/dev/test/1234",
						vgName = "test"
				))
		)
		kotlin.test.assertEquals(
				listOf(VirtualStorageDataCollection(stat = testDisk, dynamic = diskDyn)),
				unsharedDisks(
						OperationalState.fromLists(
								vStorage = listOf(testDisk),
								vStorageDyns = listOf(diskDyn),
								hosts = listOf(testHost)
						)
				)
		)
	}


	@Test
	fun unsharedDisksWithSharedDisk() {
		val diskDyn = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocations = listOf(VirtualStorageLvmAllocation(
						hostId = testHost.id,
						actualSize = testDisk.size,
						path = "/dev/test/1234",
						vgName = "test"
				))
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
		kotlin.test.assertEquals(
				listOf(),
				unsharedDisks(
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