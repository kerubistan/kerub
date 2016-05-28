package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.utils.toSize
import org.junit.Test

import org.junit.Assert.*
import java.util.UUID

class IscsiShareTest {

	val host = Host(
			id = UUID.randomUUID(),
			address = "test-1.example.com",
			publicKey = "",
			dedicated = true
	)

	val hostDyn = HostDynamic(
			id = host.id,
			status = HostStatus.Up,
			services = listOf()
	)

	val vStorage = VirtualStorageDevice(
			id = UUID.randomUUID(),
			name = "disk-1",
			expectations = listOf(),
			size = "16 GB".toSize()
	)

	val vStorageDyn = VirtualStorageDeviceDynamic(
			id = vStorage.id,
			actualSize = vStorage.size,
			allocation = VirtualStorageLvmAllocation(
					hostId = host.id,
					path = "/dev/test/" + vStorage.id
			)
	)


	@Test
	fun testTake() {
		val newState = IscsiShare(host = host, vstorage = vStorage).take(OperationalState.fromLists(
				hosts = listOf(host),
				hostDyns = listOf(hostDyn),
				vStorage = listOf(vStorage),
				vStorageDyns = listOf(vStorageDyn)
		))
		assertTrue(newState.hostDyns[host.id]!!.services.isNotEmpty())
	}
}