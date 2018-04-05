package com.github.kerubistan.kerub.planner.steps.vm

import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.expectations.CpuArchitectureExpectation
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import java.math.BigInteger

fun storageAllocationMap(state: OperationalState,
						 links: List<VirtualStorageLink>): Map<VirtualStorageDataCollection, HostDataCollection?> =
		links.map { link ->
			val vStorage = requireNotNull(state.vStorage[link.virtualStorageId])
			val dynamic = state.vStorage[vStorage.stat.id]?.dynamic
			val hostId = dynamic?.allocation?.hostId
			val hostData = if (hostId == null) null else state.hosts[hostId]
			vStorage to hostData
		}.toMap()


/**T
 * Checks if the virtual machine is <strong>technically</strong> able to run on the host.
 * It does not check on the vm, virtual storage, network expectations, that's the planner's business.
 */
fun match(
		host: HostDataCollection,
		vm: VirtualMachine,
		vStorage: Map<VirtualStorageDataCollection, HostDataCollection?> = mapOf()): Boolean {

	//host not running or not known
	if (host.dynamic == null) {
		return false
	}

	if (host.stat.capabilities?.cpus?.map { it.coreCount ?: 1 }?.sum()?.coerceAtLeast(1) ?: 1 < vm.nrOfCpus) {
		return false
	}

	if ((host.stat.capabilities?.totalMemory ?: BigInteger.ZERO) < vm.memory.min) {
		return false
	}

	if ((host.dynamic.memFree ?: BigInteger.ZERO) < vm.memory.min) {
		return false
	}

	if (vm.virtualStorageLinks.any { link -> !storageAvailable(vStorage, host) }) {
		return false
	}

	val cpuArchitectureExpectation = vm.expectations
			.firstOrNull { it is CpuArchitectureExpectation } as CpuArchitectureExpectation?

	return host.dynamic.status == HostStatus.Up &&
			(cpuArchitectureExpectation?.cpuArchitecture == null
					|| host.stat.capabilities?.cpuArchitecture == cpuArchitectureExpectation.cpuArchitecture)

}

/**
 * Storage is created and is either local OR shared on the host where it is created.
 */
fun storageAvailable(
		vStorage: Map<VirtualStorageDataCollection, HostDataCollection?>,
		host: HostDataCollection): Boolean = vStorage.all {
	val storage = it.key
	val storageHost = it.value

	return if (storageHost != null) {
		isLocalAllocation(storage, host) ||
				//or another host, but it is shared
				//either with iscsi
				isIscsiShared(storageHost, storage)
				// or with nfs mounted here
				|| isNfsShared(host, storageHost, storage)
	} else {
		false
	}

}

private fun isLocalAllocation(storage: VirtualStorageDataCollection,
							  host: HostDataCollection) =
		storage.dynamic?.allocations?.any { it.hostId == host.stat.id } ?: false

private fun isNfsShared(host: HostDataCollection,
						storageHost: HostDataCollection,
						storage: VirtualStorageDataCollection): Boolean =
		host.config?.services
				?.filterIsInstance<NfsMount>()
				?.any { mount ->
					val directory = lazy {
						storageHost.config?.services?.filterIsInstance<NfsService>()?.firstOrNull { service ->
							// where the storage has any allocations
							storage.dynamic?.allocations?.any {
								it is VirtualStorageFsAllocation
										&& it.hostId == storageHost.stat.id
										&& it.mountPoint == service.directory
							} ?: false
						}?.directory
					}
					mount.remoteHostId == storageHost.stat.id
							//and there is such nfs service on the storage host
							&& mount.remoteDirectory == directory.value
				} ?: false

private fun isIscsiShared(storageHost: HostDataCollection,
						  storage: VirtualStorageDataCollection) =
		storageHost.config?.services?.contains(IscsiService(storage.stat.id)) ?: false
