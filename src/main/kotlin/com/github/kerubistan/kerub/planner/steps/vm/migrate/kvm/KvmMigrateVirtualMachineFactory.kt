package com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.collection.VirtualMachineDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.vm.common.allNetworksAvailable
import com.github.kerubistan.kerub.planner.steps.vm.common.getVirtualNetworkConnections
import com.github.kerubistan.kerub.planner.steps.vm.match
import io.github.kerubistan.kroki.collections.concat
import kotlin.reflect.KClass

/**
 * Takes each running virtual machines and running hosts except the one the VM is running on
 * and generates steps if the host matches the requirements of the VM.
 */
object KvmMigrateVirtualMachineFactory : AbstractOperationalStepFactory<KvmMigrateVirtualMachine>() {
	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>(VirtualMachineAvailabilityExpectation::class)

	override fun produce(state: OperationalState): List<KvmMigrateVirtualMachine> =
			state.index.runningHosts.map { hostData ->
				state.index.runningVms.mapNotNull { vmData ->
					val virtualNetworkConnections by lazy { getVirtualNetworkConnections(vmData.stat, hostData) }

					if (match(hostData, vmData.stat)
							&& allStorageShared(vmData, state)
							&& allNetworksAvailable(vmData.stat, virtualNetworkConnections)) {
						val sourceId = vmData.dynamic?.hostId
						if (sourceId != hostData.stat.id) {
							KvmMigrateVirtualMachine(
									vm = vmData.stat,
									source = requireNotNull(state.hosts[sourceId]).stat,
									target = hostData.stat
							)
						} else null
					} else null
				}
			}.concat()

	private fun allStorageShared(vm: VirtualMachineDataCollection, state: OperationalState): Boolean =
			vm.stat.virtualStorageLinks.all { link ->
				val storage = requireNotNull(state.vStorage[link.virtualStorageId])
				// this attached have an allocation that is shared with some protocol
				// OR if it is read only, then it is ok if we have a copy of it on the
				// target server
				storage.dynamic?.allocations?.any { allocation ->
					val storageHost = requireNotNull(state.hosts[allocation.hostId])
					when (allocation) {
						is VirtualStorageBlockDeviceAllocation ->
							storageHost.config?.services?.any { service ->
								service is IscsiService && service.vstorageId == storage.stat.id
							} ?: false
						is VirtualStorageFsAllocation ->
							storageHost.config?.services?.any { service ->
								service is NfsService && service.directory == allocation.mountPoint
							} ?: false
						else -> TODO("Hey this is totally unexpected: $allocation")
					}
				} ?: false
			}

}