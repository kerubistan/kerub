package com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.MB
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.expectations.CpuArchitectureExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testOtherHost
import com.github.kerubistan.kerub.testVm
import org.junit.Test
import kotlin.test.assertTrue

class KvmMigrateVirtualMachineFactoryTest {
	@Test
	fun produce() {
		assertTrue("blank state should produce no steps") {
			KvmMigrateVirtualMachineFactory.produce(OperationalState.fromLists()).isEmpty()
		}
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