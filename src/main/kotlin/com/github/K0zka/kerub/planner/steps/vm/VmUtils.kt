package com.github.K0zka.kerub.planner.steps.vm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.VirtualStorageLink
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.expectations.CpuArchitectureExpectation
import java.math.BigInteger

/**
 * Checks if the virtual machine is <strong>technically</strong> able to run on the host.
 */
fun match(
		host: Host,
		dyn: HostDynamic?,
		vm: VirtualMachine,
		vStorage: Map<VirtualStorageDevice, VirtualStorageDeviceDynamic> = mapOf()): Boolean {

	//host not running or not known
	if (dyn == null) {
		return false
	}

	if (host.capabilities?.cpus?.map { it.coreCount ?: 1 }?.sum() ?: 1 < vm.nrOfCpus) {
		return false
	}

	if ((host.capabilities?.totalMemory ?: BigInteger.ZERO) < vm.memory.min) {
		return false
	}

	if ((dyn.memFree ?: BigInteger.ZERO) < vm.memory.min) {
		return false
	}

	if (vm.virtualStorageLinks.any { link -> !storageAvailable(link, vStorage, vm, host, dyn) }) {
		return false
	}

	val cpuArchitectureExpectation = vm.expectations
			.firstOrNull { it is CpuArchitectureExpectation } as CpuArchitectureExpectation?

	return dyn.status == HostStatus.Up &&
			(cpuArchitectureExpectation?.cpuArchitecture == null
					|| host.capabilities?.cpuArchitecture == cpuArchitectureExpectation?.cpuArchitecture)

}

/**
 * Storage is create and is either local OR shared on the host where it is created.
 */
fun storageAvailable(link: VirtualStorageLink, vStorage: Map<VirtualStorageDevice, VirtualStorageDeviceDynamic>, vm: VirtualMachine, host: Host, hostDynamic: HostDynamic): Boolean {
	//TODO: https://github.com/kerubistan/kerub/issues/154
	// I need the host dyn here from the host where the vstorage is
	// so meanwhile, as a placeholder...
	return true
}
