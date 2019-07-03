package com.github.kerubistan.kerub.planner.steps.storage.fs.convert.othercap

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage.Companion.pack
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVirtualDisk
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.hasAny
import com.github.kerubistan.kerub.utils.junix.common.Fedora
import com.github.kerubistan.kerub.vmUp
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class ConvertImageFactoryTest : AbstractFactoryVerifications(ConvertImageFactory) {

	@Test
	fun produce() {
		assertTrue("disk is not allocated yet") {
			ConvertImageFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							vStorage = listOf(testVirtualDisk)
					)
			).isEmpty()
		}
		assertTrue("host is not running") {
			ConvertImageFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							vStorage = listOf(testVirtualDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testVirtualDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															hostId = testHost.id,
															actualSize = 1.GB,
															mountPoint = "/kerub",
															fileName = "",
															type = VirtualDiskFormat.qcow2,
															capabilityId = testFsCapability.id
													)
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("not enough free space for conversion") {
			val cap1 = FsStorageCapability(
					id = UUID.randomUUID(), size = 1.TB, mountPoint = "/kerub-1",
					fsType = "ext4")
			val cap2 = FsStorageCapability(
					id = UUID.randomUUID(), size = 1.TB, mountPoint = "/kerub-2",
					fsType = "ext4")
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(cap1, cap2)
					)
			)
			val virtualDisk = testVirtualDisk.copy(size = 100.GB)
			ConvertImageFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(
									hostUp(host).copy(
											storageStatus = listOf(
													SimpleStorageDeviceDynamic(id = cap1.id, freeCapacity = 50.GB),
													SimpleStorageDeviceDynamic(id = cap2.id, freeCapacity = 50.GB)
											)
									)
							),
							vStorage = listOf(virtualDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = virtualDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															hostId = host.id,
															actualSize = 1.GB,
															mountPoint = "/kerub",
															fileName = "",
															type = VirtualDiskFormat.qcow2,
															capabilityId = testFsCapability.id
													)
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("disk is RW and being used") {
			val cap1 = FsStorageCapability(
					id = UUID.randomUUID(), size = 1.TB, mountPoint = "/kerub-1",
					fsType = "ext4")
			val cap2 = FsStorageCapability(
					id = UUID.randomUUID(), size = 1.TB, mountPoint = "/kerub-2",
					fsType = "ext4")
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(cap1, cap2)
					)
			)
			val virtualDisk = testVirtualDisk.copy(size = 100.GB)
			val fsAllocation = VirtualStorageFsAllocation(
					hostId = host.id,
					actualSize = 1.GB,
					mountPoint = "/kerub",
					fileName = "",
					type = VirtualDiskFormat.qcow2,
					capabilityId = testFsCapability.id
			)
			ConvertImageFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(
									HostDynamic(
											id = host.id,
											status = HostStatus.Up,
											storageStatus = listOf(
													SimpleStorageDeviceDynamic(id = cap1.id, freeCapacity = 200.GB),
													SimpleStorageDeviceDynamic(id = cap2.id, freeCapacity = 200.GB)
											)
									)
							),
							vStorage = listOf(virtualDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = virtualDisk.id,
											allocations = listOf(
													fsAllocation
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("disk is RO and being used - still we can convert") {
			val cap1 = FsStorageCapability(
					id = UUID.randomUUID(), size = 1.TB, mountPoint = "/kerub",
					fsType = "ext4")
			val cap2 = FsStorageCapability(
					id = UUID.randomUUID(), size = 1.TB, mountPoint = "/storage",
					fsType = "ext4")
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(cap1, cap2),
							os = OperatingSystem.Linux,
							distribution = pack(Fedora, "72"),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val virtualDisk = testVirtualDisk.copy(readOnly = true, size = 100.GB)
			val vm = testVm.copy(
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									virtualStorageId = virtualDisk.id,
									readOnly = true,
									device = DeviceType.disk,
									bus = BusType.sata
							)
					)
			)
			val fsAllocation = VirtualStorageFsAllocation(
					hostId = host.id,
					actualSize = 1.GB,
					mountPoint = "/kerub",
					fileName = "",
					type = VirtualDiskFormat.qcow2,
					capabilityId = testFsCapability.id
			)
			ConvertImageFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(
									hostUp(host).copy(
											storageStatus = listOf(
													SimpleStorageDeviceDynamic(id = cap1.id, freeCapacity = 200.GB),
													SimpleStorageDeviceDynamic(id = cap2.id, freeCapacity = 200.GB)
											)
									)
							),
							vms = listOf(vm),
							vmDyns = listOf(vmUp(vm, host)),
							vStorage = listOf(virtualDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = virtualDisk.id,
											allocations = listOf(
													fsAllocation
											)
									)
							)
					)
			).hasAny<ConvertImage> {
				it.host == host
						&& it.sourceAllocation == fsAllocation
						&& it.targetAllocation is VirtualStorageFsAllocation
			}
		}
		assertTrue("disk is RW and not used - let's convert") {
			val cap1 = FsStorageCapability(
					id = UUID.randomUUID(), size = 1.TB, mountPoint = "/kerub",
					fsType = "ext4")
			val cap2 = FsStorageCapability(
					id = UUID.randomUUID(), size = 1.TB, mountPoint = "/storage",
					fsType = "ext4")
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(cap1, cap2),
							os = OperatingSystem.Linux,
							distribution = pack(Fedora, "72"),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val virtualDisk = testVirtualDisk.copy(readOnly = false, size = 100.GB)
			val fsAllocation = VirtualStorageFsAllocation(
					hostId = host.id,
					actualSize = 1.GB,
					mountPoint = "/kerub",
					fileName = "",
					type = VirtualDiskFormat.qcow2,
					capabilityId = testFsCapability.id
			)
			ConvertImageFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(
									hostUp(host).copy(
											storageStatus = listOf(
													SimpleStorageDeviceDynamic(id = cap1.id, freeCapacity = 200.GB),
													SimpleStorageDeviceDynamic(id = cap2.id, freeCapacity = 200.GB)
											)
									)
							),
							vStorage = listOf(virtualDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = virtualDisk.id,
											allocations = listOf(
													fsAllocation
											)
									)
							)
					)
			).hasAny<ConvertImage> {
				it.host == host
						&& it.sourceAllocation == fsAllocation
						&& it.targetAllocation is VirtualStorageFsAllocation
			}
		}


	}
}