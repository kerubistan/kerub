package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.utils.toSize
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

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
			allocations = listOf(VirtualStorageLvmAllocation(
					hostId = host.id,
					actualSize = vStorage.size,
					path = "/dev/test/" + vStorage.id,
					vgName = "test"
			))
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
		val step = steps.single()
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