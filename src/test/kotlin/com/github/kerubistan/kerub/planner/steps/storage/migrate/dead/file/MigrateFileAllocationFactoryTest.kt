package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.file

import com.github.kerubistan.kerub.hostDown
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.SoftwarePackage.Companion.pack
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.junix.common.Centos
import com.github.kerubistan.kerub.vmUp
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import java.util.UUID.randomUUID
import kotlin.test.assertTrue

class MigrateFileAllocationFactoryTest : AbstractFactoryVerifications(MigrateFileAllocationFactory) {

	@Test
	fun produce() {
		assertTrue("both hosts upp, keys exchanged, file not used, enough free space - let's go") {
			val sourceCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					size = 1.TB,
					fsType = "ext4"
			)
			val sourceHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							distribution = pack(Centos, "7.0"),
							storageCapabilities = listOf(
									sourceCapability
							),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val sourceHostDyn = hostUp(sourceHost).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(id = sourceCapability.id, freeCapacity = 100.GB)
					)
			)
			val targetCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					size = 1.TB,
					fsType = "ext4"
			)
			val targeteHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							distribution = pack(Centos, "7.0"),
							storageCapabilities = listOf(
									targetCapability
							),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val targetHostDyn = hostUp(targeteHost).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(id = targetCapability.id, freeCapacity = 1.TB)
					)
			)
			MigrateFileAllocationFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(sourceHost, targeteHost),
							hostDyns = listOf(sourceHostDyn, targetHostDyn),
							hostCfgs = listOf(
									HostConfiguration(
											id = sourceHost.id,
											publicKey = "host-1-pubic-key"
									),
									HostConfiguration(
											id = targeteHost.id,
											acceptedPublicKeys = listOf("host-1-pubic-key")
									)
							),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															capabilityId = sourceCapability.id,
															mountPoint = sourceCapability.mountPoint,
															hostId = sourceHost.id,
															actualSize = testDisk.size,
															fileName = "${testDisk.id}.qcow2",
															type = VirtualDiskFormat.qcow2
													)
											)
									)
							)
					)
			).single().let { migrate ->
				migrate.sourceHost == sourceHost &&
						migrate.targetHost == targeteHost &&
						migrate.virtualStorage == testDisk
			}
		}
		assertTrue("keys not exchanged - no go") {
			val sourceCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					size = 1.TB,
					fsType = "ext4"
			)
			val sourceHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							distribution = pack(Centos, "7.0"),
							storageCapabilities = listOf(
									sourceCapability
							),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val sourceHostDyn = hostUp(sourceHost).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(id = sourceCapability.id, freeCapacity = 100.GB)
					)
			)
			val targetCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					size = 1.TB,
					fsType = "ext4"
			)
			val targeteHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							distribution = pack(Centos, "7.0"),
							storageCapabilities = listOf(
									targetCapability
							),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val targetHostDyn = hostUp(targeteHost).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(id = targetCapability.id, freeCapacity = 1.TB)
					)
			)
			MigrateFileAllocationFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(sourceHost, targeteHost),
							hostDyns = listOf(sourceHostDyn, targetHostDyn),
							hostCfgs = listOf(
									HostConfiguration(
											id = sourceHost.id,
											publicKey = "host-1-pubic-key"
									),
									HostConfiguration(
											id = targeteHost.id,
											acceptedPublicKeys = listOf() // not installed there
									)
							),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															capabilityId = sourceCapability.id,
															mountPoint = sourceCapability.mountPoint,
															hostId = sourceHost.id,
															actualSize = testDisk.size,
															fileName = "${testDisk.id}.qcow2",
															type = VirtualDiskFormat.qcow2
													)
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("source host down - no go") {
			val sourceCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					size = 1.TB,
					fsType = "ext4"
			)
			val sourceHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							distribution = pack(Centos, "7.0"),
							storageCapabilities = listOf(
									sourceCapability
							),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val sourceHostDyn = hostDown(sourceHost).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(id = sourceCapability.id, freeCapacity = 100.GB)
					)
			)
			val targetCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					size = 1.TB,
					fsType = "ext4"
			)
			val targeteHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							distribution = pack(Centos, "7.0"),
							storageCapabilities = listOf(
									targetCapability
							),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val targetHostDyn = hostUp(targeteHost).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(id = targetCapability.id, freeCapacity = 1.TB)
					)
			)
			MigrateFileAllocationFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(sourceHost, targeteHost),
							hostDyns = listOf(sourceHostDyn, targetHostDyn),
							hostCfgs = listOf(
									HostConfiguration(
											id = sourceHost.id,
											publicKey = "host-1-pubic-key"
									),
									HostConfiguration(
											id = targeteHost.id,
											acceptedPublicKeys = listOf("host-1-pubic-key")
									)
							),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															capabilityId = sourceCapability.id,
															mountPoint = sourceCapability.mountPoint,
															hostId = sourceHost.id,
															actualSize = testDisk.size,
															fileName = "${testDisk.id}.qcow2",
															type = VirtualDiskFormat.qcow2
													)
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("all other host down - no go") {
			val sourceCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					size = 1.TB,
					fsType = "ext4"
			)
			val sourceHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							distribution = pack(Centos, "7.0"),
							storageCapabilities = listOf(
									sourceCapability
							),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val sourceHostDyn = hostUp(sourceHost).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(id = sourceCapability.id, freeCapacity = 100.GB)
					)
			)
			val targetCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					size = 1.TB,
					fsType = "ext4"
			)
			val targeteHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							distribution = pack(Centos, "7.0"),
							storageCapabilities = listOf(
									targetCapability
							),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val targetHostDyn = hostDown(targeteHost).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(id = targetCapability.id, freeCapacity = 1.TB)
					)
			)
			MigrateFileAllocationFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(sourceHost, targeteHost),
							hostDyns = listOf(sourceHostDyn, targetHostDyn),
							hostCfgs = listOf(
									HostConfiguration(
											id = sourceHost.id,
											publicKey = "host-1-pubic-key"
									),
									HostConfiguration(
											id = targeteHost.id,
											acceptedPublicKeys = listOf("host-1-pubic-key")
									)
							),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															capabilityId = sourceCapability.id,
															mountPoint = sourceCapability.mountPoint,
															hostId = sourceHost.id,
															actualSize = testDisk.size,
															fileName = "${testDisk.id}.qcow2",
															type = VirtualDiskFormat.qcow2
													)
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("file used by a running vm - no go") {
			val sourceCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					size = 1.TB,
					fsType = "ext4"
			)
			val sourceHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							distribution = pack(Centos, "7.0"),
							storageCapabilities = listOf(
									sourceCapability
							),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val sourceHostDyn = hostUp(sourceHost).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(id = sourceCapability.id, freeCapacity = 100.GB)
					)
			)
			val targetCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					size = 1.TB,
					fsType = "ext4"
			)
			val targeteHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							distribution = pack(Centos, "7.0"),
							storageCapabilities = listOf(
									targetCapability
							),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val targetHostDyn = hostUp(targeteHost).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(id = targetCapability.id, freeCapacity = 1.TB)
					)
			)
			val vm = testVm.copy(
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									virtualStorageId = testDisk.id,
									bus = BusType.sata,
									device = DeviceType.disk,
									readOnly = false
							)
					)
			)
			MigrateFileAllocationFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(sourceHost, targeteHost),
							hostDyns = listOf(sourceHostDyn, targetHostDyn),
							hostCfgs = listOf(
									HostConfiguration(
											id = sourceHost.id,
											publicKey = "host-1-pubic-key"
									),
									HostConfiguration(
											id = targeteHost.id,
											acceptedPublicKeys = listOf("host-1-pubic-key")
									)
							),
							vms = listOf(vm),
							vmDyns = listOf(vmUp(vm, sourceHost)),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															capabilityId = sourceCapability.id,
															mountPoint = sourceCapability.mountPoint,
															hostId = sourceHost.id,
															actualSize = testDisk.size,
															fileName = "${testDisk.id}.qcow2",
															type = VirtualDiskFormat.qcow2
													)
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("file used by a running vm in ro - but still no go") {
			val sourceCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					size = 1.TB,
					fsType = "ext4"
			)
			val sourceHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							distribution = pack(Centos, "7.0"),
							storageCapabilities = listOf(
									sourceCapability
							),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val sourceHostDyn = hostUp(sourceHost).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(id = sourceCapability.id, freeCapacity = 100.GB)
					)
			)
			val targetCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					size = 1.TB,
					fsType = "ext4"
			)
			val targeteHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							distribution = pack(Centos, "7.0"),
							storageCapabilities = listOf(
									targetCapability
							),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val targetHostDyn = hostUp(targeteHost).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(id = targetCapability.id, freeCapacity = 1.TB)
					)
			)
			val vm = testVm.copy(
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									virtualStorageId = testDisk.id,
									bus = BusType.sata,
									device = DeviceType.disk,
									readOnly = true
							)
					)
			)
			MigrateFileAllocationFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(sourceHost, targeteHost),
							hostDyns = listOf(sourceHostDyn, targetHostDyn),
							hostCfgs = listOf(
									HostConfiguration(
											id = sourceHost.id,
											publicKey = "host-1-pubic-key"
									),
									HostConfiguration(
											id = targeteHost.id,
											acceptedPublicKeys = listOf("host-1-pubic-key")
									)
							),
							vms = listOf(vm),
							vmDyns = listOf(vmUp(vm, sourceHost)),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															capabilityId = sourceCapability.id,
															mountPoint = sourceCapability.mountPoint,
															hostId = sourceHost.id,
															actualSize = testDisk.size,
															fileName = "${testDisk.id}.qcow2",
															type = VirtualDiskFormat.qcow2
													)
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("no free space on other host - no go") {
			val sourceCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					size = 1.TB,
					fsType = "ext4"
			)
			val sourceHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							distribution = pack(Centos, "7.0"),
							storageCapabilities = listOf(
									sourceCapability
							),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val sourceHostDyn = hostUp(sourceHost).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(id = sourceCapability.id, freeCapacity = 100.GB)
					)
			)
			val targetCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					size = 1.TB,
					fsType = "ext4"
			)
			val targeteHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							distribution = pack(Centos, "7.0"),
							storageCapabilities = listOf(
									targetCapability
							),
							installedSoftware = listOf(
									pack("qemu-img", "1.2.3")
							)
					)
			)
			val targetHostDyn = hostUp(targeteHost).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(id = targetCapability.id, freeCapacity = 0.TB)
					)
			)
			MigrateFileAllocationFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(sourceHost, targeteHost),
							hostDyns = listOf(sourceHostDyn, targetHostDyn),
							hostCfgs = listOf(
									HostConfiguration(
											id = sourceHost.id,
											publicKey = "host-1-pubic-key"
									),
									HostConfiguration(
											id = targeteHost.id,
											acceptedPublicKeys = listOf("host-1-pubic-key")
									)
							),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															capabilityId = sourceCapability.id,
															mountPoint = sourceCapability.mountPoint,
															hostId = sourceHost.id,
															actualSize = testDisk.size,
															fileName = "${testDisk.id}.qcow2",
															type = VirtualDiskFormat.qcow2
													)
											)
									)
							)
					)
			).isEmpty()
		}

	}

}