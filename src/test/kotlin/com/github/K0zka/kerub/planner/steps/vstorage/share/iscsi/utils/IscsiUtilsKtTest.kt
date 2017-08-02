package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.utils

import com.github.K0zka.kerub.model.collection.VirtualStorageDataCollection
import com.github.K0zka.kerub.model.config.HostConfiguration
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.model.services.IscsiService
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.testDisk
import com.github.K0zka.kerub.testHost
import com.github.K0zka.kerub.utils.genPassword
import org.junit.Test

class IscsiUtilsKtTest {

	@Test
	fun unsharedDisks() {
		val diskDyn = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocation = VirtualStorageLvmAllocation(
						hostId = testHost.id,
						actualSize = testDisk.size,
						path = "/dev/test/1234"
				)
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
				allocation = VirtualStorageLvmAllocation(
						hostId = testHost.id,
						actualSize = testDisk.size,
						path = "/dev/test/1234"
				)
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