package com.github.kerubistan.kerub.planner.steps.storage.share.nfs

import com.github.kerubistan.kerub.MB
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.controller.config.StorageTechnologiesConfig
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import kotlin.test.assertTrue

class ShareNfsFactoryTest : AbstractFactoryVerifications(ShareNfsFactory) {

	@Test
	fun produce() {
		assertTrue("when nfs disabled, no share steps") {
			ShareNfsFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											services = listOf(NfsDaemonService())
									)
							),
							vStorage = listOf(),
							config = ControllerConfig(
									storageTechnologies = StorageTechnologiesConfig(
											nfsEnabled = false
									)
							)
					)
			).isEmpty()
		}
		assertTrue("when nfs enabled, produce steps") {
			ShareNfsFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											services = listOf(NfsDaemonService())
									)
							),
							vStorage = listOf(
									testDisk
							),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															hostId = testHost.id,
															actualSize = 100.MB,
															mountPoint = "/kerub",
															fileName = "${testDisk.id}.qcow",
															type = VirtualDiskFormat.qcow2,
															capabilityId = testFsCapability.id
													)
											)
									)
							),
							config = ControllerConfig(
									storageTechnologies = StorageTechnologiesConfig(
											nfsEnabled = true
									)
							)
					)
			) == listOf(ShareNfs(host = testHost, directory = "/kerub"))
		}
		assertTrue("if it is already shared, do not share it again") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testFsCapability)
					)
			)
			ShareNfsFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostCfgs = listOf(
									HostConfiguration(
											id = host.id,
											services = listOf(NfsDaemonService(), NfsService("/kerub", write = true))
									)
							),
							vStorage = listOf(
									testDisk
							),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															hostId = testHost.id,
															actualSize = 100.MB,
															mountPoint = testFsCapability.mountPoint,
															fileName = "${testDisk.id}.qcow",
															type = VirtualDiskFormat.qcow2,
															capabilityId = testFsCapability.id
													)
											)
									)
							),
							config = ControllerConfig(
									storageTechnologies = StorageTechnologiesConfig(
											nfsEnabled = true
									)
							)
					)
			).isEmpty()
		}
	}
}