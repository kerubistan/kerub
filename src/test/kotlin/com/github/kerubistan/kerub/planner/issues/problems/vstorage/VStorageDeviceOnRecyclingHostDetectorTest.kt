package com.github.kerubistan.kerub.planner.issues.problems.vstorage

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testLvmCapability
import com.github.kerubistan.kerub.testVm
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class VStorageDeviceOnRecyclingHostDetectorTest {
	@Test
	fun detect() {
		assertTrue("blank state - no problem") {
			VStorageDeviceOnRecyclingHostDetector.detect(Plan(state = OperationalState.fromLists())).isEmpty()
		}

		assertTrue("No hosts being recycled - no problem") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testLvmCapability)
					)
			)
			val vm = testVm
			val disk = testDisk
			val allocation = VirtualStorageFsAllocation(
					hostId = host.id,
					fileName = "/var/storage/testdisk.qcow2",
					type = VirtualDiskFormat.qcow2,
					actualSize = 16.GB,
					mountPoint = "/var/storage/",
					capabilityId = testLvmCapability.id
			)
			VStorageDeviceOnRecyclingHostDetector.detect(Plan(
					state = OperationalState.fromLists(
							vms = listOf(vm),
							vmDyns = listOf(VirtualMachineDynamic(
									id = vm.id,
									status = VirtualMachineStatus.Up,
									memoryUsed = 1.GB,
									hostId = host.id
							)),
							vStorage = listOf(disk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = disk.id,
											allocations = listOf(allocation)
									)
							),
							hosts = listOf(host),
							hostDyns = listOf(HostDynamic(
									id = host.id,
									status = HostStatus.Up,
									memFree = 16.GB
							))
					)
			)).isEmpty()
		}

		assertTrue("host being recycled - problem should be detected") {
			val host = testHost.copy(
					recycling = true,
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testLvmCapability)
					)
			)
			val vm = testVm
			val disk = testDisk
			val allocation = VirtualStorageFsAllocation(
					hostId = host.id,
					fileName = "/var/storage/testdisk.qcow2",
					type = VirtualDiskFormat.qcow2,
					actualSize = 16.GB,
					mountPoint = "/var/storage/",
					capabilityId = testLvmCapability.id
			)
			VStorageDeviceOnRecyclingHostDetector.detect(Plan(
					state = OperationalState.fromLists(
							vms = listOf(vm),
							vmDyns = listOf(VirtualMachineDynamic(
									id = vm.id,
									status = VirtualMachineStatus.Up,
									memoryUsed = 1.GB,
									hostId = host.id
							)),
							vStorage = listOf(disk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = disk.id,
											allocations = listOf(allocation)
									)
							),
							hosts = listOf(host),
							hostDyns = listOf(HostDynamic(
									id = host.id,
									status = HostStatus.Up,
									memFree = 16.GB
							))
					)
			)) == listOf(VStorageDeviceOnRecyclingHost(vstorage = disk, host = host, allocation = allocation))
		}

		assertTrue("another host being recycled - no problem") {
			val host = testHost.copy(
					id = UUID.randomUUID(),
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testLvmCapability)
					)
			)
			val otherHost = testHost.copy(
					recycling = true,
					id = UUID.randomUUID()
			)
			val vm = testVm
			val disk = testDisk
			val allocation = VirtualStorageFsAllocation(
					hostId = host.id,
					fileName = "/var/storage/testdisk.qcow2",
					type = VirtualDiskFormat.qcow2,
					actualSize = 16.GB,
					mountPoint = "/var/storage/",
					capabilityId = testLvmCapability.id
			)
			VStorageDeviceOnRecyclingHostDetector.detect(Plan(
					state = OperationalState.fromLists(
							vms = listOf(vm),
							vmDyns = listOf(VirtualMachineDynamic(
									id = vm.id,
									status = VirtualMachineStatus.Up,
									memoryUsed = 1.GB,
									hostId = host.id
							)),
							vStorage = listOf(disk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = disk.id,
											allocations = listOf(allocation)
									)
							),
							hosts = listOf(host, otherHost),
							hostDyns = listOf(
									HostDynamic(
									id = host.id,
									status = HostStatus.Up,
									memFree = 16.GB
									),
									HostDynamic(
											id = otherHost.id,
											status = HostStatus.Up,
											memFree = 16.GB
									)
							)
					)
			)).isEmpty()
		}


	}

}