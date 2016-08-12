package com.github.K0zka.kerub.planner.steps.vm.start.kvm

import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.model.hardware.ProcessorInformation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vm.start.kvm.KvmStartVirtualMachine
import com.github.K0zka.kerub.planner.steps.vm.start.kvm.KvmStartVirtualMachineFactory
import com.github.K0zka.kerub.utils.toSize
import org.junit.Assert
import org.junit.Test
import java.util.UUID

class KvmStartVirtualMachineFactoryTest {

	val vm = VirtualMachine(
			name = "test-vm"
	                       )
	val host = Host(
			address = "host-1.example.com",
			dedicated = true,
			id = UUID.randomUUID(),
			publicKey = "",
	        capabilities = HostCapabilities(
			        cpus = listOf(ProcessorInformation(
					        coreCount = 2,
			                threadCount = 4,
			                maxSpeedMhz = 3000,
			                flags = listOf(),
			                manufacturer = "TEST",
			                version = "TEST CPU",
			                socket = "",
			                voltage = null
			                                          )),
	                cpuArchitecture = "x86_64",
	                totalMemory = "8 GB".toSize(),
	                installedSoftware = listOf(
							SoftwarePackage(name = "qemu-kvm", version = Version.fromVersionString("2.4.1")),
							SoftwarePackage(name = "libvirt", version = Version.fromVersionString("1.2.18"))
					),
	                devices = listOf(),
	                os = OperatingSystem.Linux,
	                system = null,
	                chassis = null,
	                distribution = null
	                                       )
	               )

	val hostDyn = HostDynamic(
			id = host.id,
			status = HostStatus.Up,
	        memFree = "8 GB".toSize()
	                         )


	val vmDyn = VirtualMachineDynamic(
			id = vm.id,
			status = VirtualMachineStatus.Up,
			hostId = host.id,
			memoryUsed = "1 GB".toSize()
	                                 )

	@Test
	fun produceWithoutBlankState() {
		val steps = KvmStartVirtualMachineFactory.produce(OperationalState.fromLists())
		Assert.assertTrue(steps.isEmpty())
	}

	@Test
	fun produceWithoutHost() {
		val steps = KvmStartVirtualMachineFactory.produce(OperationalState.fromLists())
		Assert.assertTrue(steps.isEmpty())
	}

	@Test
	fun produceWithoutRunningHosts() {
		val steps = KvmStartVirtualMachineFactory.produce(
				OperationalState.fromLists(
						vms = listOf(vm),
						hosts = listOf(host),
						vmDyns = listOf()))
		Assert.assertTrue(steps.isEmpty())
	}

	@Test
	fun produceWithVmRunning() {
		val steps = KvmStartVirtualMachineFactory.produce(
				OperationalState.fromLists(
						vms = listOf(vm.copy(
								expectations = listOf(VirtualMachineAvailabilityExpectation(
										up = true,
								        level = ExpectationLevel.DealBreaker
								                                                    )
						                    ))),
						hosts = listOf(host),
						vmDyns = listOf(vmDyn),
						hostDyns = listOf(hostDyn)))
		Assert.assertTrue(steps.isEmpty())
	}

	@Test
	fun produceWithVmNotRunning() {
		val vmToRun = vm.copy(
				expectations = listOf(VirtualMachineAvailabilityExpectation(
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
		Assert.assertTrue(steps.isNotEmpty())
		Assert.assertTrue(steps.any { it is KvmStartVirtualMachine
				&& it.vm == vmToRun
				&& it.host == host } )
	}

}