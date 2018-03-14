package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs

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
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import kotlin.test.assertTrue

class ShareNfsFactoryTest {

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
													VirtualStorageFsAllocation(hostId = testHost.id,
																			   actualSize = 100.MB,
																			   mountPoint = "/kerub",
																			   fileName = "${testDisk.id}.qcow",
																			   type = VirtualDiskFormat.qcow2)
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
			ShareNfsFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											services = listOf(NfsDaemonService(), NfsService("/kerub"))
									)
							),
							vStorage = listOf(
									testDisk
							),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(hostId = testHost.id,
																			   actualSize = 100.MB,
																			   mountPoint = "/kerub",
																			   fileName = "${testDisk.id}.qcow",
																			   type = VirtualDiskFormat.qcow2)
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