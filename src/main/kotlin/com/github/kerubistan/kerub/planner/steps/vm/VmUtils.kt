package com.github.kerubistan.kerub.planner.steps.vm

import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.VirtualStorageLinkInfo
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.expectations.CpuArchitectureExpectation
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import io.github.kerubistan.kroki.collections.join
import java.math.BigInteger
import java.util.UUID

/**
 * Give a list of storage link information of the virtual storage links
 * #targetHostId the host where the service should be available
 */
fun virtualStorageLinkInfo(
		state: OperationalState, links: List<VirtualStorageLink>, targetHostId: UUID
): List<VirtualStorageLinkInfo> =
		links.mapNotNull { link ->
			state.vStorage[link.virtualStorageId]?.let { storage ->
				storage.dynamic?.allocations?.filter { state.hosts[it.hostId]?.dynamic?.status == HostStatus.Up }
						?.mapNotNull { allocation ->
							val storageHost = state.hosts.getValue(allocation.hostId)

							if (storageHost.stat.id == targetHostId) {
								VirtualStorageLinkInfo(
										device = storage,
										link = link,
										allocation = allocation,
										storageHost = storageHost,
										// no storage service needed, direct access on same host
										hostServiceUsed = null
								)
							} else {
								// either we have a storage service, or no link
								// but it would be nice to have this elsewhere
								// there can be lots of types of such services
								storageHost.config?.services?.firstOrNull { service ->
									when (service) {
										is IscsiService ->
											service.vstorageId == storage.stat.id
										is NfsService ->
											allocation is VirtualStorageFsAllocation
													&& allocation.mountPoint.startsWith(service.directory)
													// and on the other side, it NEEDS an NFS mount
													&& state.hosts[targetHostId]?.config?.services
													?.filterIsInstance<NfsMount>()
													?.any {
														it.remoteHostId == storageHost.stat.id
																&& it.remoteDirectory == allocation.mountPoint
													} ?: false
										else ->
											false
									}
								}?.let {
									VirtualStorageLinkInfo(
											device = storage,
											link = link,
											allocation = allocation,
											storageHost = storageHost,
											hostServiceUsed = it
									)
								}
							}

						}
			}
		}.join()

fun allStorageAvailable(vm: VirtualMachine, links: List<VirtualStorageLinkInfo>): Boolean =
		vm.virtualStorageLinks.all { vmStorageLink ->
			links.any { linkInfo ->
				linkInfo.device.stat.id == vmStorageLink.virtualStorageId
			}
		}


/**T
 * Checks if the virtual machine is <strong>technically</strong> able to run on the host.
 * It does not check on the vm, virtual storage, network expectations, that's the planner's business.
 */
fun match(host: HostDataCollection, vm: VirtualMachine): Boolean {

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

	val cpuArchitectureExpectation = vm.expectations
			.firstOrNull { it is CpuArchitectureExpectation } as CpuArchitectureExpectation?

	return host.dynamic.status == HostStatus.Up &&
			(cpuArchitectureExpectation?.cpuArchitecture == null
					|| host.stat.capabilities?.cpuArchitecture == cpuArchitectureExpectation.cpuArchitecture)

}
