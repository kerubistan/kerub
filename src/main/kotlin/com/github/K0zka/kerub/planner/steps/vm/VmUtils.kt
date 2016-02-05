package com.github.K0zka.kerub.planner.steps.vm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.expectations.CpuArchitectureExpectation
import java.math.BigInteger

/**
 * Checks if the virtual machine is <strong>technically</strong> able to run on the host.
 */
fun match(host: Host, dyn: HostDynamic?, vm: VirtualMachine): Boolean {

	if (host.capabilities?.cpus?.map { it.coreCount ?: 1 }?.sum() ?: 1 < vm.nrOfCpus) {
		return false
	}

	if ((host.capabilities?.totalMemory ?: BigInteger.ZERO) < vm.memory.min) {
		return false
	}

	if ((dyn?.memFree ?: BigInteger.ZERO) < vm.memory.min) {
		return false
	}

	val cpuArchitectureExpectation = vm.expectations
			.firstOrNull { it is CpuArchitectureExpectation } as CpuArchitectureExpectation?

	return dyn?.status == HostStatus.Up &&
			(cpuArchitectureExpectation?.cpuArchitecture == null
					|| host.capabilities?.cpuArchitecture == cpuArchitectureExpectation?.cpuArchitecture)

}
