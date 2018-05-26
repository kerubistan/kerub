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
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
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

		fun <T : Entity<I>, I> mapById(entities: List<T>): Map<I, T>
				= entities.associateBy { it.id }

		private fun mapHostData(hosts: List<Host> = listOf(),
								hostDyns: List<HostDynamic> = listOf(),
								hostCfgs: List<HostConfiguration> = listOf()): Map<UUID, HostDataCollection> {
			val hostDynMap = mapById(hostDyns)
			val hostCfgMap = mapById(hostCfgs)
			return hosts.map {
				it.id to HostDataCollection(it, hostDynMap[it.id], hostCfgMap[it.id] ?: HostConfiguration(id = it.id))
			}.toMap()
		}

		fun <I, T : Entity<I>, D : DynamicEntity, C : DataCollection<T, D>>
				mapToCollection(staticData: List<T>, dynamicData: List<D>, transform: (static: T, dynamic: D?) -> C): Map<I, C> {
			val dynMap: Map<UUID, D> = mapById(dynamicData)
			return staticData.map { it.id to transform(it, dynMap[it.id as UUID]) }.toMap()
		}

		fun fromLists(hosts: List<Host> = listOf(),
					  hostDyns: List<HostDynamic> = listOf(),
					  hostCfgs: List<HostConfiguration> = listOf(),
					  vms: List<VirtualMachine> = listOf(),
					  vmDyns: List<VirtualMachineDynamic> = listOf(),
					  vStorage: List<VirtualStorageDevice> = listOf(),
					  vStorageDyns: List<VirtualStorageDeviceDynamic> = listOf(),
					  reservations: List<Reservation<*>> = listOf(),
					  config: ControllerConfig = ControllerConfig()
		) =
				OperationalState(
						hosts = mapHostData(hosts, hostDyns, hostCfgs),
						vms = mapToCollection(vms, vmDyns) { stat, dyn -> VirtualMachineDataCollection(stat, dyn) },
						vStorage = mapToCollection(vStorage, vStorageDyns) { stat, dyn -> VirtualStorageDataCollection(stat, dyn) },
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
					.map { vms[it.dynamic!!.id]?.stat }.filterNotNull()

	fun isVmRunning(vm: VirtualMachine): Boolean {
		val dyn = vms[vm.id]?.dynamic
		return dyn != null && dyn.status == VirtualMachineStatus.Up
	}

	fun vmHost(vm: VirtualMachine): Host? = vmHost(vm.id)

	fun vmHost(vmId: UUID): Host? {
		val dyn = vms[vmId]?.dynamic
		return if (dyn == null) null else hosts[dyn.hostId]?.stat
	}

	fun virtualStorageToCheck(): List<VirtualStorageDevice> =
			vStorage.values.filterNot {
			reservations.contains(VirtualStorageReservation(it.stat))
		}.map { it.stat }

}