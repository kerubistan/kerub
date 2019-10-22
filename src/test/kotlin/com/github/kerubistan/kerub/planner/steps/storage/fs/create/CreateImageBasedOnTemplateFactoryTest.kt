package com.github.kerubistan.kerub.planner.steps.storage.fs.create

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage.Companion.pack
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.Centos
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.KB
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID
import kotlin.test.assertTrue

class CreateImageBasedOnTemplateFactoryTest : AbstractFactoryVerifications(CreateImageBasedOnTemplateFactory) {

	@Test
	fun produce() {

		assertTrue("no free space on the mount - do not try to create") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testFsCapability),
							os = OperatingSystem.Linux,
							distribution = pack(Centos, "7.5"),
							installedSoftware = listOf(
									pack("qemu-img", "1,2,3")
							)
					)
			)
			val templateDisk = VirtualStorageDevice(
					id = randomUUID(),
					size = 3.GB,
					readOnly = true,
					name = "template disk"
			)
			val disk = testDisk.copy(
					size = 5.GB,
					expectations = listOf(
							StorageAvailabilityExpectation(),
							CloneOfStorageExpectation(sourceStorageId = templateDisk.id)
					)
			)
			factory.produce(OperationalState.fromLists(
					hosts = listOf(host),
					hostDyns = listOf(
							hostUp(host).copy(
									storageStatus = listOf(
											SimpleStorageDeviceDynamic(
													id = testFsCapability.id,
													freeCapacity = 1.KB
											)
									)
							)
					),
					vStorage = listOf(disk, templateDisk),
					vStorageDyns = listOf(
							VirtualStorageDeviceDynamic(
									id = templateDisk.id,
									allocations = listOf(
											VirtualStorageFsAllocation(
													hostId = host.id,
													type = VirtualDiskFormat.qcow2,
													actualSize = 3.GB,
													fileName = "${templateDisk.id}.qcow2",
													capabilityId = testFsCapability.id,
													mountPoint = testFsCapability.mountPoint
											)
									)
							)
					)
			)).isEmpty()
		}


		assertTrue("the template only has raw allocation - can't use it as backing file") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testFsCapability),
							os = OperatingSystem.Linux,
							distribution = pack(Centos, "7.5"),
							installedSoftware = listOf(
									pack("qemu-img", "1,2,3")
							)
					)
			)
			val templateDisk = VirtualStorageDevice(
					id = randomUUID(),
					size = 3.GB,
					readOnly = true,
					name = "template disk"
			)
			val disk = testDisk.copy(
					size = 5.GB,
					expectations = listOf(
							StorageAvailabilityExpectation(),
							CloneOfStorageExpectation(sourceStorageId = templateDisk.id)
					)
			)
			factory.produce(OperationalState.fromLists(
					hosts = listOf(host),
					hostDyns = listOf(
							hostUp(host).copy(
									storageStatus = listOf(
											SimpleStorageDeviceDynamic(
													id = testFsCapability.id,
													freeCapacity = 100.GB
											)
									)
							)
					),
					vStorage = listOf(disk, templateDisk),
					vStorageDyns = listOf(
							VirtualStorageDeviceDynamic(
									id = templateDisk.id,
									allocations = listOf(
											VirtualStorageFsAllocation(
													hostId = host.id,
													type = VirtualDiskFormat.raw,
													actualSize = 3.GB,
													fileName = "${templateDisk.id}.raw",
													capabilityId = testFsCapability.id,
													mountPoint = testFsCapability.mountPoint
											)
									)
							)
					)
			)).isEmpty()
		}

		assertTrue("have template, have free space, let's go") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testFsCapability),
							os = OperatingSystem.Linux,
							distribution = pack(Centos, "7.5"),
							installedSoftware = listOf(
									pack("qemu-img", "1,2,3")
							)
					)
			)
			val templateDisk = VirtualStorageDevice(
					id = randomUUID(),
					size = 3.GB,
					readOnly = true,
					name = "template disk"
			)
			val disk = testDisk.copy(
					size = 5.GB,
					expectations = listOf(
							StorageAvailabilityExpectation(),
							CloneOfStorageExpectation(sourceStorageId = templateDisk.id)
					)
			)
			factory.produce(OperationalState.fromLists(
					hosts = listOf(host),
					hostDyns = listOf(
							hostUp(host).copy(
									storageStatus = listOf(
											SimpleStorageDeviceDynamic(
													id = testFsCapability.id,
													freeCapacity = 100.GB
											)
									)
							)
					),
					vStorage = listOf(disk, templateDisk),
					vStorageDyns = listOf(
							VirtualStorageDeviceDynamic(
									id = templateDisk.id,
									allocations = listOf(
											VirtualStorageFsAllocation(
													hostId = host.id,
													type = VirtualDiskFormat.qcow2,
													actualSize = 3.GB,
													fileName = "${templateDisk.id}.qcow2",
													capabilityId = testFsCapability.id,
													mountPoint = testFsCapability.mountPoint
											)
									)
							)
					)
			)).isNotEmpty()
		}
	}
}