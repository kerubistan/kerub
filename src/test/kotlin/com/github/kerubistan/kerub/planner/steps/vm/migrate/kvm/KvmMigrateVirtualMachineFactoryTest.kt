package com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm

import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.expectations.CpuArchitectureExpectation
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testLvmCapability
import com.github.kerubistan.kerub.testOtherHost
import com.github.kerubistan.kerub.testVirtualDisk
import com.github.kerubistan.kerub.testVm
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.MB
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class KvmMigrateVirtualMachineFactoryTest : AbstractFactoryVerifications(KvmMigrateVirtualMachineFactory) {
	@Test
	fun produce() {
		assertTrue("if there are no running vms, no steps") {
			KvmMigrateVirtualMachineFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost, testOtherHost),
							hostDyns = listOf(
									HostDynamic(id = testHost.id, status = HostStatus.Up),
									HostDynamic(id = testOtherHost.id, status = HostStatus.Up)
							)
					)
			).isEmpty()
		}
		assertTrue("if there is only one host, nowhere to go") {
			KvmMigrateVirtualMachineFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(
									HostDynamic(id = testHost.id, status = HostStatus.Up)
							),
							vms = listOf(testVm),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = testVm.id,
											status = VirtualMachineStatus.Up,
											hostId = testHost.id,
											memoryUsed = 512.MB
									)
							)
					)
			).isEmpty()
		}

		assertTrue("two hosts, no virtual storage, let's offer to migrate") {
			val testHost1 = testHost.copy(
					capabilities = testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							totalMemory = 128.GB
					)
			)
			val testHost2 = testOtherHost.copy(
					capabilities = testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							totalMemory = 128.GB
					)
			)

			KvmMigrateVirtualMachineFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost1, testHost2),
							hostDyns = listOf(
									HostDynamic(id = testHost1.id, status = HostStatus.Up, memFree = 120.GB),
									HostDynamic(id = testHost2.id, status = HostStatus.Up, memFree = 120.GB)
							),
							vms = listOf(testVm),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = testVm.id,
											status = VirtualMachineStatus.Up,
											hostId = testHost1.id,
											memoryUsed = 128.MB
									)
							)
					)
			) == listOf(KvmMigrateVirtualMachine(vm = testVm, source = testHost1, target = testHost2))
		}

		assertTrue("two hosts, vm with virtual storage, not shared") {
			val host1FsCapability = FsStorageCapability(
					id = UUID.randomUUID(),
					size = 1.TB,
					mountPoint = "/kerub",
					fsType = "ext4"
			)
			val testHost1 = testHost.copy(
					capabilities = testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							totalMemory = 128.GB,
							storageCapabilities = listOf(host1FsCapability)
					)
			)
			val testHost2 = testOtherHost.copy(
					capabilities = testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							totalMemory = 128.GB
					)
			)

			val vm = testVm.copy(
					expectations = listOf(CpuArchitectureExpectation(cpuArchitecture = "X86_64")),
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									virtualStorageId = testDisk.id,
									readOnly = false,
									device = DeviceType.disk,
									bus = BusType.sata
							)
					)
			)

			KvmMigrateVirtualMachineFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost1, testHost2),
							hostDyns = listOf(
									HostDynamic(id = testHost1.id, status = HostStatus.Up, memFree = 120.GB),
									HostDynamic(id = testHost2.id, status = HostStatus.Up, memFree = 120.GB)
							),
							vms = listOf(vm),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = testVm.id,
											status = VirtualMachineStatus.Up,
											hostId = testHost1.id,
											memoryUsed = 128.MB
									)
							),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															hostId = testHost1.id,
															mountPoint = host1FsCapability.mountPoint,
															type = VirtualDiskFormat.qcow2,
															fileName = "${host1FsCapability.mountPoint}/${testDisk.id}.qcow2",
															actualSize = 20.GB,
															capabilityId = testFsCapability.id
													)
											)
									))
					)
			).isEmpty()
		}

		assertTrue("two hosts, vm with virtual storage, shared with NFS - should be ok to migrate") {
			val host1FsCapability = FsStorageCapability(
					id = UUID.randomUUID(),
					size = 1.TB,
					mountPoint = "/kerub",
					fsType = "ext4"
			)
			val testHost1 = testHost.copy(
					capabilities = testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							totalMemory = 128.GB,
							storageCapabilities = listOf(host1FsCapability)
					)
			)
			val testHost2 = testOtherHost.copy(
					capabilities = testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							totalMemory = 128.GB
					)
			)

			val vm = testVm.copy(
					expectations = listOf(CpuArchitectureExpectation(cpuArchitecture = "X86_64")),
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									virtualStorageId = testDisk.id,
									readOnly = false,
									device = DeviceType.disk,
									bus = BusType.sata
							)
					)
			)

			KvmMigrateVirtualMachineFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost1, testHost2),
							hostDyns = listOf(
									HostDynamic(id = testHost1.id, status = HostStatus.Up, memFree = 120.GB),
									HostDynamic(id = testHost2.id, status = HostStatus.Up, memFree = 120.GB)
							),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost1.id,
											services = listOf(
													NfsDaemonService(),
													NfsService(directory = "/kerub", write = true)
											)
									)
							),
							vms = listOf(vm),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = testVm.id,
											status = VirtualMachineStatus.Up,
											hostId = testHost1.id,
											memoryUsed = 128.MB
									)
							),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															hostId = testHost1.id,
															mountPoint = host1FsCapability.mountPoint,
															type = VirtualDiskFormat.qcow2,
															fileName =
																"${host1FsCapability.mountPoint}/${testDisk.id}/.qcow2",
															actualSize = 20.GB,
															capabilityId = testFsCapability.id
													)
											)
									))
					)
			) == listOf(KvmMigrateVirtualMachine(vm = vm, source = testHost1, target = testHost2))
		}

		assertTrue("two hosts, vm with virtual storage, shared with ISCSI - should be ok to migrate") {
			val host1FsCapability = FsStorageCapability(
					id = UUID.randomUUID(),
					size = 1.TB,
					mountPoint = "/kerub",
					fsType = "ext4"
			)
			val testHost1 = testHost.copy(
					capabilities = testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							totalMemory = 128.GB,
							storageCapabilities = listOf(host1FsCapability)
					)
			)
			val testHost2 = testOtherHost.copy(
					capabilities = testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							totalMemory = 128.GB
					)
			)

			val vm = testVm.copy(
					expectations = listOf(CpuArchitectureExpectation(cpuArchitecture = "X86_64")),
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									virtualStorageId = testDisk.id,
									readOnly = false,
									device = DeviceType.disk,
									bus = BusType.sata
							)
					)
			)

			KvmMigrateVirtualMachineFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost1, testHost2),
							hostDyns = listOf(
									HostDynamic(id = testHost1.id, status = HostStatus.Up, memFree = 120.GB),
									HostDynamic(id = testHost2.id, status = HostStatus.Up, memFree = 120.GB)
							),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost1.id,
											services = listOf(
													IscsiService(vstorageId = testDisk.id)
											)
									)
							),
							vms = listOf(vm),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = testVm.id,
											status = VirtualMachineStatus.Up,
											hostId = testHost1.id,
											memoryUsed = 128.MB
									)
							),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															hostId = testHost1.id,
															actualSize = 20.GB,
															vgName = "kerub-data",
															path = "dev/kerub-data/${testDisk.id}",
															capabilityId = testLvmCapability.id
													)
											)
									))
					)
			) == listOf(KvmMigrateVirtualMachine(vm = vm, source = testHost1, target = testHost2))
		}

		assertTrue("two hosts, vm with two virtual storages, only one shared with ISCSI - can't migrate") {
			val host1FsCapability = FsStorageCapability(
					id = UUID.randomUUID(),
					size = 1.TB,
					mountPoint = "/kerub",
					fsType = "ext4"
			)
			val testHost1 = testHost.copy(
					capabilities = testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							totalMemory = 128.GB,
							storageCapabilities = listOf(host1FsCapability)
					)
			)
			val testHost2 = testOtherHost.copy(
					capabilities = testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							totalMemory = 128.GB
					)
			)

			val vm = testVm.copy(
					expectations = listOf(CpuArchitectureExpectation(cpuArchitecture = "X86_64")),
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									virtualStorageId = testDisk.id,
									readOnly = false,
									device = DeviceType.disk,
									bus = BusType.sata
							),
							VirtualStorageLink(
									virtualStorageId = testVirtualDisk.id,
									readOnly = false,
									device = DeviceType.disk,
									bus = BusType.sata
							)

					)
			)

			KvmMigrateVirtualMachineFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost1, testHost2),
							hostDyns = listOf(
									HostDynamic(id = testHost1.id, status = HostStatus.Up, memFree = 120.GB),
									HostDynamic(id = testHost2.id, status = HostStatus.Up, memFree = 120.GB)
							),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost1.id,
											services = listOf(
													IscsiService(vstorageId = testDisk.id)
											)
									)
							),
							vms = listOf(vm),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = testVm.id,
											status = VirtualMachineStatus.Up,
											hostId = testHost1.id,
											memoryUsed = 128.MB
									)
							),
							vStorage = listOf(testDisk, testVirtualDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															hostId = testHost1.id,
															actualSize = 20.GB,
															vgName = "kerub-data",
															path = "dev/kerub-data/${testDisk.id}",
															capabilityId = testLvmCapability.id
													)
											)
									),
									VirtualStorageDeviceDynamic(
											id = testVirtualDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															hostId = testHost1.id,
															actualSize = 20.GB,
															vgName = "kerub-data",
															path = "dev/kerub-data/${testVirtualDisk.id}",
															capabilityId = testLvmCapability.id
													)
											)
									)
							)
					)
			).isEmpty()
		}

		assertTrue("two hosts, no virtual storage but second host does not match") {
			val testHost1 = testHost.copy(
					capabilities = testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							totalMemory = 128.GB
					)
			)
			val testHost2 = testOtherHost.copy(
					capabilities = testHostCapabilities.copy(
							cpuArchitecture = "Aarch64",
							os = OperatingSystem.Linux,
							totalMemory = 128.GB
					)
			)

			val vm = testVm.copy(
					expectations = listOf(CpuArchitectureExpectation(cpuArchitecture = "X86_64"))
			)
			KvmMigrateVirtualMachineFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost1, testHost2),
							hostDyns = listOf(
									HostDynamic(id = testHost1.id, status = HostStatus.Up, memFree = 120.GB),
									HostDynamic(id = testHost2.id, status = HostStatus.Up, memFree = 120.GB)
							),
							vms = listOf(vm),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = vm.id,
											status = VirtualMachineStatus.Up,
											hostId = testHost1.id,
											memoryUsed = 128.MB
									)
							)
					)
			).isEmpty()
		}

	}
}