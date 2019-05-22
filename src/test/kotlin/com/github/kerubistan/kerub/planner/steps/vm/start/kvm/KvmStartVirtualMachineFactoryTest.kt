package com.github.kerubistan.kerub.planner.steps.vm.start.kvm

import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.model.hardware.ProcessorInformation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.junix.virt.virsh.LibvirtArch
import com.github.kerubistan.kerub.utils.junix.virt.virsh.LibvirtCapabilities
import com.github.kerubistan.kerub.utils.junix.virt.virsh.LibvirtGuest
import io.github.kerubistan.kroki.size.GB
import org.junit.Test
import java.util.UUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KvmStartVirtualMachineFactoryTest : AbstractFactoryVerifications(KvmStartVirtualMachineFactory) {

	private val vm = VirtualMachine(
			name = "test-vm"
	)
	private val host = Host(
			address = "host-1.example.com",
			dedicated = true,
			id = UUID.randomUUID(),
			publicKey = "",
			capabilities = HostCapabilities(
					cpus = listOf(
							ProcessorInformation(
									coreCount = 2,
									threadCount = 4,
									maxSpeedMhz = 3000,
									flags = listOf("vmx"),
									manufacturer = "TEST",
									version = "TEST CPU",
									socket = "",
									voltage = null
							)),
					cpuArchitecture = "x86_64",
					totalMemory = 8.GB,
					installedSoftware = listOf(
							SoftwarePackage(name = "qemu-kvm", version = Version.fromVersionString("2.4.1")),
							SoftwarePackage(name = "libvirt-bin", version = Version.fromVersionString("1.2.18"))
					),
					devices = listOf(),
					os = OperatingSystem.Linux,
					distribution = SoftwarePackage("Ubuntu", version = Version.fromVersionString("17.04")),
					system = null,
					chassis = null,
					hypervisorCapabilities = listOf(
							LibvirtCapabilities(
									guests = listOf(
											LibvirtGuest(
													osType = "hvm",
													arch = LibvirtArch(
															name = "x86_64",
															wordsize = 64,
															emulator = "/usr/bin/qemu-kvm"
													)
											)
									)
							)
					)
			)
	)

	private val hostDyn = HostDynamic(
			id = host.id,
			status = HostStatus.Up,
			memFree = 8.GB
	)


	private val vmDyn = VirtualMachineDynamic(
			id = vm.id,
			status = VirtualMachineStatus.Up,
			hostId = host.id,
			memoryUsed = 1.GB
	)

	@Test
	fun produceWithoutRunningHosts() {
		val steps = KvmStartVirtualMachineFactory.produce(
				OperationalState.fromLists(
						vms = listOf(vm),
						hosts = listOf(host),
						vmDyns = listOf()))
		assertTrue(steps.isEmpty())
	}

	@Test
	fun produceWithVmRunning() {
		val steps = KvmStartVirtualMachineFactory.produce(
				OperationalState.fromLists(
						vms = listOf(
								vm.copy(
										expectations = listOf(
												VirtualMachineAvailabilityExpectation(
														up = true,
														level = ExpectationLevel.DealBreaker
												)
										))),
						hosts = listOf(host),
						vmDyns = listOf(vmDyn),
						hostDyns = listOf(hostDyn)))
		assertTrue(steps.isEmpty())
	}

	@Test
	fun produceWithVmNotRunning() {
		val vmToRun = vm.copy(
				expectations = listOf(
						VirtualMachineAvailabilityExpectation(
								up = true,
								level = ExpectationLevel.DealBreaker
						)
				))
		val steps = KvmStartVirtualMachineFactory.produce(
				OperationalState.fromLists(
						vms = listOf(vmToRun),
						hosts = listOf(host),
						vmDyns = listOf(),
						hostDyns = listOf(hostDyn)))
		assertTrue(steps.isNotEmpty())
		assertTrue(steps.any {
			it.vm == vmToRun
					&& it.host == host
		})
	}

	@Test
	fun isKvmCapable() {
		assertFalse("if no capabilities, the VM may not be ok on the host") {
			KvmStartVirtualMachineFactory.isKvmCapable(listOf(), testVm)
		}

		assertTrue("matching capability") {
			KvmStartVirtualMachineFactory.isKvmCapable(
					listOf(
							LibvirtCapabilities(
									guests = listOf(
											LibvirtGuest(
													arch =
													LibvirtArch(
															name = "x86_64", emulator = "/usr/bin/qemu-kvm",
															wordsize = 64),
													osType = "hvm"
											)
									)
							)),
					testVm)
		}

	}

	@Test
	fun isKvmInstalled() {
		assertTrue("kvm installed") {
			KvmStartVirtualMachineFactory.isKvmInstalled(host)
		}
		assertFalse("kvm not installed") {
			KvmStartVirtualMachineFactory.isKvmInstalled(testHost)
		}
	}

}