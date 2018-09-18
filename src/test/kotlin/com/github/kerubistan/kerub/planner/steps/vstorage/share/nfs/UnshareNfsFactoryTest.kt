package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.controller.config.StorageTechnologiesConfig
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class UnshareNfsFactoryTest {

	@Test
	fun produce() {

		assertTrue("blank state - no steps") {
			UnshareNfsFactory.produce(
					OperationalState.fromLists()
			).isEmpty()
		}

		assertTrue("controller can unshare even if nfs not enabled") {
			UnshareNfsFactory.produce(
					OperationalState.fromLists(
							config = ControllerConfig(
									storageTechnologies = StorageTechnologiesConfig(
											nfsEnabled = false
									)
							),
							hosts = listOf(testHost),
							hostCfgs = listOf(HostConfiguration(
									id = testHost.id,
									services = listOf(NfsDaemonService(), NfsService("/kerub", write = true))
							))
					)
			) == listOf(UnshareNfs(directory = "/kerub", host = testHost))
		}

		assertTrue("don't do anything if there is nothing to do - no directories shared") {
			UnshareNfsFactory.produce(
					OperationalState.fromLists(
							config = ControllerConfig(
									storageTechnologies = StorageTechnologiesConfig(
											nfsEnabled = true
									)
							),
							hosts = listOf(testHost),
							hostCfgs = listOf(HostConfiguration(
									id = testHost.id,
									services = listOf(NfsDaemonService())
							))
					)
			).isEmpty()
		}

		assertTrue("unshare if not used") {
			UnshareNfsFactory.produce(
					OperationalState.fromLists(
							config = ControllerConfig(
									storageTechnologies = StorageTechnologiesConfig(
											nfsEnabled = false
									)
							),
							hosts = listOf(testHost),
							hostCfgs = listOf(HostConfiguration(
									id = testHost.id,
									services = listOf(NfsDaemonService(), NfsService("/kerub", write = true))
							))
					)
			) == listOf(UnshareNfs(directory = "/kerub", host = testHost))
		}

		assertTrue("do not unshare if it is mounted") {
			val vm = testVm.copy(
					virtualStorageLinks = listOf(
							VirtualStorageLink(virtualStorageId = testDisk.id, bus = BusType.virtio,
											   device = DeviceType.cdrom, readOnly = true))
			)
			val nfsServer = testHost.copy(
					id = UUID.randomUUID()
			)
			val nfsClient = testHost.copy(
					id = UUID.randomUUID()
			)

			UnshareNfsFactory.produce(
					OperationalState.fromLists(
							config = ControllerConfig(
									storageTechnologies = StorageTechnologiesConfig(
											nfsEnabled = false
									)
							),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															hostId = nfsServer.id,
															mountPoint = "/kerub",
															type = VirtualDiskFormat.qcow2,
															fileName = "",
															actualSize = 100.GB
													)
											)
									)
							),
							hosts = listOf(nfsServer, nfsClient),
							hostCfgs = listOf(
									HostConfiguration(id = nfsServer.id,
													  services = listOf(
															  NfsDaemonService(),
															  NfsService("/kerub", write = true))
									),
									HostConfiguration(
											id = nfsClient.id,
											services = listOf(NfsMount(remoteHostId = nfsServer.id,
																	   remoteDirectory = "/kerub",
																	   localDirectory = "/mnt"))
									)
							)
					)
			).isEmpty()
		}


	}
}