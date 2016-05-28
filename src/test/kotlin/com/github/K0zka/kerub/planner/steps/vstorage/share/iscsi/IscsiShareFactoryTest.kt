package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.model.services.IscsiService
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.services.socket.services
import com.github.K0zka.kerub.utils.only
import com.github.K0zka.kerub.utils.toSize
import org.junit.Test

import org.junit.Assert.*
import java.util.UUID

class IscsiShareFactoryTest {

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
	fun produceSingleShareable() {
		val steps = IscsiShareFactory.produce(
				OperationalState.fromLists(
						hosts = listOf(host),
						hostDyns = listOf(hostDyn),
						vStorage = listOf(vStorage),
						vStorageDyns = listOf(vStorageDyn)
				)
		)
		val step = steps.only()
		assertTrue(step.host == host)
		assertTrue(step.vstorage == vStorage)
	}

	@Test
	fun produceAlreadyShared() {
		@Test
		fun produceSingleShareable() {
			val steps = IscsiShareFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostDyn.copy(
									services = listOf(
											IscsiService(vStorage.id)
									)
							)),
							vStorage = listOf(vStorage),
							vStorageDyns = listOf(vStorageDyn)
					)
			)
			assertTrue(steps.isEmpty())
		}

	}

}