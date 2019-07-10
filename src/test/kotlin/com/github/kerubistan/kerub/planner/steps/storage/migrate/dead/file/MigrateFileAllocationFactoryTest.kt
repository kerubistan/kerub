package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.file

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
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
							storageCapabilities = listOf(
									sourceCapability
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
							storageCapabilities = listOf(
									targetCapability
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
			TODO()
		}
		assertTrue("keys not exchanged - no go") {
			TODO()
		}
		assertTrue("allocation host down - no go") {
			TODO()
		}
		assertTrue("all other host down - no go") {
			TODO()
		}
		assertTrue("file used by a running vm - no go") {
			TODO()
		}
		assertTrue("file used by a running vm in ro - but still no go") {
			TODO()
		}
		assertTrue("no free space on other host - no go") {
			TODO()
		}
		assertTrue("no free space on other host - no go") {
			TODO()
		}

	}

}