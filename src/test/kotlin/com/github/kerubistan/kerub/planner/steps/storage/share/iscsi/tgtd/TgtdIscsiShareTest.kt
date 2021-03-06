package com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.tgtd

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testLvmCapability
import io.github.kerubistan.kroki.size.GB
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class TgtdIscsiShareTest : OperationalStepVerifications() {

	private val host = Host(
			id = UUID.randomUUID(),
			address = "test-1.example.com",
			publicKey = "",
			dedicated = true
	)

	private val hostDyn = HostDynamic(
			id = host.id,
			status = HostStatus.Up
	)

	private val vStorage = VirtualStorageDevice(
			id = UUID.randomUUID(),
			name = "disk-1",
			expectations = listOf(),
			size = 16.GB
	)

	private val vStorageDyn = VirtualStorageDeviceDynamic(
			id = vStorage.id,
			allocations = listOf(VirtualStorageLvmAllocation(
					hostId = host.id,
					actualSize = vStorage.size,
					path = "/dev/test/" + vStorage.id,
					vgName = "test",
					capabilityId = testLvmCapability.id
			))
	)

	override val step = TgtdIscsiShare(
			host = host,
			vstorage = vStorage,
			allocation = VirtualStorageLvmAllocation(
					vgName = "test",
					actualSize = testDisk.size,
					hostId = testHost.id,
					path = "",
					capabilityId = testLvmCapability.id
			)
	)

	@Test
	fun testTake() {
		val newState = TgtdIscsiShare(
				host = host,
				vstorage = vStorage,
				allocation = VirtualStorageLvmAllocation(
						vgName = "test",
						actualSize = testDisk.size,
						hostId = testHost.id,
						path = "",
						capabilityId = testLvmCapability.id
				)
		)
				.take(OperationalState.fromLists(
						hosts = listOf(host),
						hostDyns = listOf(hostDyn),
						vStorage = listOf(vStorage),
						vStorageDyns = listOf(vStorageDyn)
				))
		assertTrue(newState.hosts.getValue(host.id).config!!.services.isNotEmpty())
	}

	@Test
	fun isInverseOf() {
		assertTrue("find inverse operation") {
			val allocation = VirtualStorageLvmAllocation(
					vgName = "test",
					actualSize = testDisk.size,
					hostId = testHost.id,
					path = "",
					capabilityId = testLvmCapability.id
			)
			val share = TgtdIscsiShare(
					host = testHost,
					allocation = allocation,
					vstorage = testDisk
			)
			val unshare = TgtdIscsiUnshare(host = testHost, vstorage = testDisk, allocation = allocation)
			unshare.isInverseOf(share)
		}
	}

}