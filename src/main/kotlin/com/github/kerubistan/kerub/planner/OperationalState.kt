package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.Pool
import com.github.kerubistan.kerub.model.Template
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.VirtualNetwork
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.collection.DataCollection
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.collection.VirtualMachineDataCollection
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.dynamic.DynamicEntity
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.utils.byId
import java.util.UUID

data class OperationalState(
		val hosts: Map<UUID, HostDataCollection> = mapOf(),
		val vms: Map<UUID, VirtualMachineDataCollection> = mapOf(),
		val vStorage: Map<UUID, VirtualStorageDataCollection> = mapOf(),
		val vNet: Map<UUID, VirtualNetwork> = mapOf(),
		val pools: Map<UUID, Pool> = mapOf(),
		val templates: Map<UUID, Template> = mapOf(),
		val reservations: List<Reservation<*>> = listOf(),
		val controllerConfig: ControllerConfig = ControllerConfig()
) {

	companion object {

		fun <T : Entity<I>, I> mapById(entities: List<T>): Map<I, T> = entities.associateBy { it.id }

		private fun mapHostData(
				hosts: List<Host> = listOf(),
				hostDyns: List<HostDynamic> = listOf(),
				hostCfgs: List<HostConfiguration> = listOf()
		): Map<UUID, HostDataCollection> {
			val hostDynMap = mapById(hostDyns)
			val hostCfgMap = mapById(hostCfgs)
			return hosts.map {
				it.id to HostDataCollection(it, hostDynMap[it.id], hostCfgMap[it.id] ?: HostConfiguration(id = it.id))
			}.toMap()
		}

		fun <I, T : Entity<I>, D : DynamicEntity, C : DataCollection<I, T, D>>
				mapToCollection(staticData: List<T>, dynamicData: List<D>, transform: (static: T, dynamic: D?) -> C): Map<I, C> {
			val dynMap: Map<UUID, D> = mapById(dynamicData)
			return staticData.map { transform(it, dynMap[it.id as UUID]) }.byId()
		}

		fun fromLists(
				hosts: List<Host> = listOf(),
				hostDyns: List<HostDynamic> = listOf(),
				hostCfgs: List<HostConfiguration> = listOf(),
				vms: List<VirtualMachine> = listOf(),
				vmDyns: List<VirtualMachineDynamic> = listOf(),
				vStorage: List<VirtualStorageDevice> = listOf(),
				vStorageDyns: List<VirtualStorageDeviceDynamic> = listOf(),
				pools: List<Pool> = listOf(),
				templates: List<Template> = listOf(),
				reservations: List<Reservation<*>> = listOf(),
				config: ControllerConfig = ControllerConfig()
		) =
				OperationalState(
						hosts = mapHostData(hosts, hostDyns, hostCfgs),
						vms = mapToCollection(vms, vmDyns) { stat, dyn -> VirtualMachineDataCollection(stat, dyn) },
						vStorage = mapToCollection(vStorage, vStorageDyns) { stat, dyn ->
							VirtualStorageDataCollection(
									stat,
									dyn)
						},
						pools = pools.associateBy { it.id },
						templates = templates.associateBy { it.id },
						reservations = reservations,
						controllerConfig = config
				)
	}

	fun vmDataOnHost(hostId: UUID): List<VirtualMachineDataCollection> =
			vms.values
					.filter { it.dynamic?.status == VirtualMachineStatus.Up && it.dynamic.hostId == hostId }

	fun vmsOnHost(hostId: UUID): List<VirtualMachine> =
			vms.values
					.filter { it.dynamic?.status == VirtualMachineStatus.Up && it.dynamic.hostId == hostId }
					.mapNotNull { vms[it.dynamic!!.id]?.stat }

	fun isVmRunning(vm: VirtualMachine): Boolean {
		val dyn = vms[vm.id]?.dynamic
		return dyn != null && dyn.status == VirtualMachineStatus.Up
	}

	fun vmHost(vm: VirtualMachine): Host? = vmHost(vm.id)

	fun vmHost(vmId: UUID): Host? {
		val dyn = vms[vmId]?.dynamic
		return if (dyn == null) null else hosts[dyn.hostId]?.stat
	}

	val runningHosts by lazy { hosts.values.filter { it.dynamic?.status == HostStatus.Up } }

	operator fun <T> Collection<T>?.contains(element: T): Boolean =
			this?.contains(element) ?: false

	/**
	 * Map of host id to IDs of the hosts where the public key is accepted.
	 */
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
				if(acceptedByServer.isEmpty()) null else client.id to acceptedByServer
			}
		}.toMap()
	}

	val runningHostIds by lazy { runningHosts.map { it.stat.id }.toSet() }

	val recyclingHosts by lazy {
		hosts.values
				.filter { it.stat.recycling }
				.associateBy { it.stat.id }
	}

	val runningVms by lazy {
		vms.values.filter {
			it.dynamic?.status == VirtualMachineStatus.Up
		}
	}

	val vmsThatMustStart by lazy {
		vms.values.filter { vm ->
			vm.stat.expectations.any { expectation ->
				expectation is VirtualMachineAvailabilityExpectation
						&& expectation.up
			} && vm.dynamic?.status != VirtualMachineStatus.Up
		}
	}

	val allocatedStorage by lazy {
		vStorage.values.filter { it.dynamic?.allocations?.isNotEmpty() ?: false }
	}

}