package com.github.K0zka.kerub.planner.steps.vm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.VirtualStorageLink
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.expectations.CpuArchitectureExpectation
import com.github.K0zka.kerub.model.services.IscsiService
import com.github.K0zka.kerub.planner.OperationalState
import java.math.BigInteger

fun storageAllocationMap(state: OperationalState) =
		state.vStorage.values.map {
			vStorage ->
			val dynamic = state.vStorageDyns[vStorage.id]
			val host = if (dynamic?.allocation?.hostId == null ) null else state.hosts[dynamic!!.allocation.hostId]
			val hostDyn = if (host == null) null else state.hostDyns[host.id]
			vStorage to (dynamic to hostDyn)
		}.toMap()


/**
 * Checks if the virtual machine is <strong>technically</strong> able to run on the host.
 * It does not check on the vm, virtual storage, network expectations, that's the planner's business.
 */
fun match(
		host: Host,
		dyn: HostDynamic?,
		vm: VirtualMachine,
		vStorage: Map<VirtualStorageDevice, Pair<VirtualStorageDeviceDynamic?, HostDynamic?>> = mapOf()): Boolean {

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
fun storageAvailable(
		link: VirtualStorageLink,
		vStorage: Map<VirtualStorageDevice, Pair<VirtualStorageDeviceDynamic?, HostDynamic?>>,
		vm: VirtualMachine,
		host: Host,
		hostDynamic: HostDynamic): Boolean {

	return vStorage.all {
		val device = it.key
		val storageDyn = it.value.first
		val storageHostDyn = it.value.second

		storageHostDyn?.id == host.id ||
		storageHostDyn?.services?.contains( IscsiService(device.id) ) ?: false
	}
}
