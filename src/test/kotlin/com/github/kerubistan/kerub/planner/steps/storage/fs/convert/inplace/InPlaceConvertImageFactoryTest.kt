package com.github.kerubistan.kerub.planner.steps.storage.fs.convert.inplace

import com.github.kerubistan.kerub.diskAllocated
import com.github.kerubistan.kerub.hostDown
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testLvmCapability
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.vmUp
import org.junit.Test
import kotlin.test.assertTrue

class InPlaceConvertImageFactoryTest : AbstractFactoryVerifications(InPlaceConvertImageFactory) {
	@Test
	fun produce() {
		assertTrue("disk not allocated - no steps") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testFsCapability)
					)
			)
			factory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostUp(host)),
							vStorage = listOf(testDisk)
					)
			).isEmpty()
		}
		assertTrue("disk allocated on lvm - no steps, we can't have other than RAW on block devices") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testLvmCapability)
					)
			)
			factory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostUp(host)),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(diskAllocated(testDisk, host, testLvmCapability))
					)
			).isEmpty()
		}
		assertTrue("if the virtual storage is being used, no step should be generated") {
			val vm = testVm.copy(
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									virtualStorageId = testDisk.id,
									device = DeviceType.disk,
									bus = BusType.sata,
									readOnly = false
							)
					)
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testFsCapability)
					)
			)
			factory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostUp(host)),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									diskAllocated(
											testDisk,
											host,
											testFsCapability,
											VirtualDiskFormat.qcow2)),
							vms = listOf(vm),
							vmDyns = listOf(vmUp(vm, host))
					)
			).isEmpty()
		}
		assertTrue("if the virtual storage is not used, but the host is down - no step") {
			val vm = testVm.copy(
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									virtualStorageId = testDisk.id,
									device = DeviceType.disk,
									bus = BusType.sata,
									readOnly = false
							)
					)
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testFsCapability)
					)
			)
			factory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostDown(host)),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									diskAllocated(
											testDisk,
											host,
											testFsCapability,
											VirtualDiskFormat.qcow2)),
							vms = listOf(vm)
					)
			).isEmpty()
		}

		assertTrue("if the virtual storage is not used, step should be generated") {
			val vm = testVm.copy(
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									virtualStorageId = testDisk.id,
									device = DeviceType.disk,
									bus = BusType.sata,
									readOnly = false
							)
					)
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testFsCapability)
					)
			)
			factory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostUp(host)),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									diskAllocated(
											testDisk,
											host,
											testFsCapability,
											VirtualDiskFormat.qcow2)),
							vms = listOf(vm)
					)
			).isNotEmpty()
		}
	}
}