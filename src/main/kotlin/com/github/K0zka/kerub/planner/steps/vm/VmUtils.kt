package com.github.K0zka.kerub.planner.steps.vm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualStorageLink
import com.github.K0zka.kerub.model.collection.HostDataCollection
import com.github.K0zka.kerub.model.collection.VirtualStorageDataCollection
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.expectations.CpuArchitectureExpectation
import com.github.K0zka.kerub.model.services.IscsiService
import com.github.K0zka.kerub.planner.OperationalState
import java.math.BigInteger

fun storageAllocationMap(state: OperationalState, links: List<VirtualStorageLink>): Map<VirtualStorageDataCollection, HostDataCollection?> =
		links.map {
			link ->
			val vStorage = requireNotNull(state.vStorage[link.virtualStorageId])
			val dynamic = state.vStorage[vStorage.stat.id]?.dynamic
			val hostId = dynamic?.allocation?.hostId
			val hostData = if (hostId == null) null else state.hosts[hostId]
			vStorage to hostData
		}.toMap()


/**
 * Checks if the virtual machine is <strong>technically</strong> able to run on the host.
 * It does not check on the vm, virtual storage, network expectations, that's the planner's business.
 */
fun match(
		host: Host,
		dyn: HostDynamic?,
		vm: VirtualMachine,
		vStorage: Map<VirtualStorageDataCollection, HostDataCollection?> = mapOf()): Boolean {

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

	if (vm.virtualStorageLinks.any { link -> !storageAvailable(vStorage, host) }) {
		return false
	}

	val cpuArchitectureExpectation = vm.expectations
			.firstOrNull { it is CpuArchitectureExpectation } as CpuArchitectureExpectation?

	return dyn.status == HostStatus.Up &&
			(cpuArchitectureExpectation?.cpuArchitecture == null
					|| host.capabilities?.cpuArchitecture == cpuArchitectureExpectation.cpuArchitecture)

}

/**
 * Storage is created and is either local OR shared on the host where it is created.
 */
fun storageAvailable(
		vStorage: Map<VirtualStorageDataCollection, HostDataCollection?>,
		host: Host): Boolean {

	return vStorage.all {
		val storage = it.key
		val storageHost = it.value

		//either it is on the same host
		storage.dynamic?.allocation?.hostId == host.id ||
				//or another host, but it is shared
				storageHost?.config?.services?.contains(IscsiService(storage.stat.id)) ?: false
	}
}
