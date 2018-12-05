package com.github.kerubistan.kerub.planner.steps.vstorage.mount

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVirtualDisk
import com.github.kerubistan.kerub.testVm
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class UnmountNfsFactoryTest {

	@Test
	fun produce() {
		assertTrue("no mounts, no steps") {
			UnmountNfsFactory.produce(OperationalState.fromLists()).isEmpty()
		}
		assertTrue("no mounts, no steps") {
			UnmountNfsFactory.produce(OperationalState.fromLists(
					hosts = listOf(testHost)
			)).isEmpty()
		}
		assertTrue("unmount a share that is not used for anything") {
			val nfsClient = testHost.copy(id= UUID.randomUUID())
			val nfsServer = testHost.copy(id= UUID.randomUUID())
			UnmountNfsFactory.produce(OperationalState.fromLists(
					hosts = listOf(nfsClient, nfsServer),
					hostDyns = listOf(
							HostDynamic(id = nfsClient.id, status = HostStatus.Up),
							HostDynamic(id = nfsServer.id, status = HostStatus.Up)
					),
					hostCfgs = listOf(
							HostConfiguration(
									id = nfsServer.id,
									services = listOf(NfsDaemonService(), NfsService(directory = "/kerub", write = true))
							),
							HostConfiguration(
									id = nfsClient.id,
									services = listOf(NfsMount(remoteDirectory = "/kerub", localDirectory = "/mnt",
															   remoteHostId = nfsServer.id))
							)
					)
			)) == listOf(UnmountNfs(host = nfsClient, mountDir = "/mnt"))
		}
		assertTrue("keep a share mounted if it is used") {
			val nfsClient = testHost.copy(id = UUID.randomUUID(), address = "client.example.com")
			val nfsServer = testHost.copy(id = UUID.randomUUID(), address = "server.example.com")
			val vm = testVm.copy(
					id = UUID.randomUUID(),
					virtualStorageLinks = listOf(VirtualStorageLink(
							virtualStorageId = testVirtualDisk.id,
							bus = BusType.sata
					))
			)
			UnmountNfsFactory.produce(OperationalState.fromLists(
					hosts = listOf(nfsClient, nfsServer),
					hostDyns = listOf(
							HostDynamic(id = nfsClient.id, status = HostStatus.Up),
							HostDynamic(id = nfsServer.id, status = HostStatus.Up)
					),
					hostCfgs = listOf(
							HostConfiguration(
									id = nfsServer.id,
									services = listOf(NfsDaemonService(), NfsService(directory = "/kerub", write = true))
							),
							HostConfiguration(
									id = nfsClient.id,
									services = listOf(NfsMount(remoteDirectory = "/kerub", localDirectory = "/mnt",
															   remoteHostId = nfsServer.id))
							)
					),
					vStorage = listOf(testVirtualDisk),
					vStorageDyns = listOf(
							VirtualStorageDeviceDynamic(
									id = testVirtualDisk.id,
									allocations = listOf(
											VirtualStorageFsAllocation(
													hostId = nfsServer.id,
													mountPoint = "/kerub",
													fileName = "blah.qcow2",
													actualSize = 1.GB,
													type = VirtualDiskFormat.qcow2,
													capabilityId = testFsCapability.id
											)
									)
							)
					),
					vms = listOf(vm),
					vmDyns = listOf(
							VirtualMachineDynamic(
									id = vm.id,
									status = VirtualMachineStatus.Up,
									memoryUsed = 1.GB,
									hostId = nfsClient.id
							)
					)
			)).isEmpty()
		}
	}
}