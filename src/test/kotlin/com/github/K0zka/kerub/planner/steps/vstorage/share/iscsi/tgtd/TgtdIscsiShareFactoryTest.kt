package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.model.VirtualStorageDevice
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
import com.github.K0zka.kerub.utils.only
import com.github.K0zka.kerub.utils.toSize
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class TgtdIscsiShareFactoryTest {

	val host = Host(
			id = UUID.randomUUID(),
			address = "test-1.example.com",
			publicKey = "",
			dedicated = true,
			capabilities = HostCapabilities(
					distribution = SoftwarePackage("Fedora", Version.fromVersionString("23")),
					installedSoftware = listOf(
							SoftwarePackage("scsi-target-utils", Version.fromVersionString("1.0.55"))
					),
					cpuArchitecture = "X86_64",
					totalMemory = "1 GB".toSize()
			)
	)

	val hostDyn = HostDynamic(
			id = host.id,
			status = HostStatus.Up
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
		val steps = TgtdIscsiShareFactory.produce(
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
		val steps = TgtdIscsiShareFactory.produce(
				OperationalState.fromLists(
						hosts = listOf(host),
						hostCfgs = listOf(HostConfiguration(
								id = host.id,
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