package com.github.K0zka.kerub.planner.steps.vm.start.virtualbox

import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.testHost
import com.github.K0zka.kerub.testHostCapabilities
import com.github.K0zka.kerub.testVm
import com.github.K0zka.kerub.utils.toSize
import org.junit.Test
import kotlin.test.assertTrue

class VirtualBoxStartVirtualMachineFactoryTest {
	@Test
	fun produce() {
		assertTrue {
			val vm = testVm.copy(
					expectations = listOf(
							VirtualMachineAvailabilityExpectation()
					)
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							installedSoftware = listOf(
									SoftwarePackage(
											"virtualbox-ose",
											Version.fromVersionString("5.0.1")
									)
							),
							cpuArchitecture = "X86_64",
							totalMemory = "256 GB".toSize()
					)
			)
			val steps = VirtualBoxStartVirtualMachineFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(
									HostDynamic(
											id = host.id,
											memFree = "128 GB".toSize()
									)
							),
							vms = listOf(vm)
					)
			)
			steps == listOf(VirtualBoxStartVirtualMachine(vm = vm, host = host))
		}
	}

}