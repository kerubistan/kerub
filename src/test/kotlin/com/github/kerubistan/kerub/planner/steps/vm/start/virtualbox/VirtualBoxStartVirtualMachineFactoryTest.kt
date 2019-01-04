package com.github.kerubistan.kerub.planner.steps.vm.start.virtualbox

import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.toSize
import org.junit.Test
import kotlin.test.assertTrue

class VirtualBoxStartVirtualMachineFactoryTest : AbstractFactoryVerifications(VirtualBoxStartVirtualMachineFactory) {
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