package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.config.OvsGrePort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.model.devices.NetworkDevice
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.utils.contains
import com.github.kerubistan.kerub.utils.hasAny
import com.github.kerubistan.kerub.utils.mapInstances
import io.github.kerubistan.kroki.collections.concat
import io.github.kerubistan.kroki.collections.groupsBy
import java.util.UUID

class OperationalStateIndex(private val indexOf: OperationalState) {

	// -----
	// hosts
	// -----

	val runningHosts by lazy { indexOf.hosts.values.filter { it.dynamic?.status == HostStatus.Up } }

	val hostsByAddress by lazy {
		indexOf.hosts.values.associateBy { it.stat.address }
	}

	val runningHostIds by lazy { runningHosts.map { it.stat.id }.toSet() }

	val connectionTargets by lazy {
		runningHosts.mapNotNull { client ->
			if (client.config?.publicKey == null)
				null
			else {
				val acceptedByServer = runningHosts.mapNotNull { server ->
					if (client.config.publicKey in server.config?.acceptedPublicKeys)
						server.stat
					else null
				}
				if (acceptedByServer.isEmpty()) null else client.id to acceptedByServer
			}
		}.toMap()
	}

	val recyclingHosts by lazy {
		indexOf.hosts.values
				.filter { it.stat.recycling }
				.associateBy { it.stat.id }
	}

	// ---
	// VMs
	// ---

	val vmsWithAvailabilityExpectation by lazy {
		indexOf.vms.values.filter { vm ->
			vm.stat.expectations.any { expectation ->
				expectation is VirtualMachineAvailabilityExpectation
						&& expectation.up
			}
		}
	}

	val runningVms by lazy {
		runningVmsToHost.keys.map { vmId -> indexOf.vms.getValue(vmId) }
	}

	val runningVmsToHost by lazy {
		indexOf.vms.values.mapNotNull {
			if (it.dynamic?.status == VirtualMachineStatus.Up) {
				it.id to it.dynamic.hostId
			} else null
		}.toMap()
	}

	val vmsThatMustStart by lazy {
		indexOf.vms.values.filter { vm ->
			vm.stat.expectations.any { expectation ->
				expectation is VirtualMachineAvailabilityExpectation
						&& expectation.up
			} && vm.dynamic?.status != VirtualMachineStatus.Up
		}
	}

	// ---------------
	// virtual storage
	// ---------------

	val allocatedStorage by lazy {
		indexOf.vStorage.values.filter { it.dynamic?.allocations?.isNotEmpty() ?: false }
	}

	val storageCloneRequirement by lazy {
		indexOf.vStorage.values.filter { it.stat.expectations.hasAny<CloneOfStorageExpectation>() }
	}

	val virtualStorageNotAllocated by lazy {
		indexOf.vStorage.values.filter { it.dynamic?.allocations?.isEmpty() ?: true }
				.filter { storage ->
					storage.stat.expectations.any {
						it is StorageAvailabilityExpectation
					}
							||
							vmsThatMustStart.any { vm ->
								vm.stat.virtualStorageLinks.any { link ->
									link.virtualStorageId == storage.stat.id
								}
							}
				}.map { it.stat }

	}

	// ----------------
	// virtual networks
	// ----------------

	val virtualNetworksNeeded by lazy {
		vmsWithAvailabilityExpectation.mapNotNull { vm ->
			vm.stat.devices.mapNotNull { device ->
				if (device is NetworkDevice) {
					device.networkId
				} else null
			}
		}.concat().toSet()
	}

	/**
	 * Map of virtual network Id -> set of host
	 */
	val hostsByVirtualNetworks: Map<UUID, Set<HostDataCollection>> by lazy {
		runningHosts.groupsBy {
			it.config?.networkConfiguration?.map { netConfig -> netConfig.virtualNetworkId } ?: listOf()
		}
	}

	val hostVirtualNetworks by lazy {
		runningHosts.mapNotNull { host ->
			if (host.config == null || host.config.networkConfiguration.isEmpty()) {
				null
			} else {
				host.id to host.config.networkConfiguration.map { it.virtualNetworkId }.toSet()
			}
		}.toMap()
	}

	/**
	 * Map of source hostId -> ( network Id -> target host id)
	 */
	val hostVirtualNetworkConnections: Map<UUID, Map<UUID, Set<UUID>>> by lazy {
		runningHosts.mapNotNull { sourceHost ->
			if (sourceHost.config == null) null else {
				val networkIdsToRemoteHostIds = sourceHost.config.networkConfiguration.mapInstances { ovsConfig: OvsNetworkConfiguration ->
					val remoteHostIds = ovsConfig.ports.mapInstances { grePort: OvsGrePort ->
						hostsByAddress[grePort.remoteAddress]?.id
					}.toSet()
					if (remoteHostIds.isEmpty()) null else
						ovsConfig.virtualNetworkId to remoteHostIds
				}.toMap()
				if (networkIdsToRemoteHostIds.isEmpty()) null else
					sourceHost.id to networkIdsToRemoteHostIds
			}
		}.toMap()
	}


}