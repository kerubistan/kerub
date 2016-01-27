package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.expectations.*
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.VmReservation
import com.github.k0zka.finder4j.backtrack.State
import java.util.UUID

data class OperationalState(
		val hosts: Map<UUID, Host> = mapOf(),
		val hostDyns: Map<UUID, HostDynamic> = mapOf(),
		val vms: Map<UUID, VirtualMachine> = mapOf(),
		val vmDyns: Map<UUID, VirtualMachineDynamic> = mapOf(),
		val vStorage: Map<UUID, VirtualStorageDevice> = mapOf(),
		val vStorageDyns: Map<UUID, VirtualStorageDeviceDynamic> = mapOf(),
		val reservations : List<Reservation<*>> = listOf()
) : State {

	companion object {

		fun <T : Entity<I>, I> mapById(entities: List<T>): Map<I, T>
				= entities.toMapBy { it.id }

		fun fromLists(hosts: List<Host> = listOf(),
					  hostDyns: List<HostDynamic> = listOf(),
					  vms: List<VirtualMachine> = listOf(),
					  vmDyns: List<VirtualMachineDynamic> = listOf(),
					  vStorage: List<VirtualStorageDevice> = listOf(),
					  vStorageDyns: List<VirtualStorageDeviceDynamic> = listOf(),
					  reservations : List<Reservation<*>> = listOf()
		) =
				OperationalState(
						hosts = mapById(hosts),
						hostDyns = mapById(hostDyns),
						vms = mapById(vms),
						vmDyns = mapById(vmDyns),
						vStorage = mapById(vStorage),
						vStorageDyns = mapById(vStorageDyns),
						reservations = reservations
				)
	}

	fun vmsOnHost(hostId: UUID): List<VirtualMachine> {
		return vmDyns.values
				.filter { it.status == VirtualMachineStatus.Up && it.hostId == hostId }
				.map { vms[it.id] }.filterNotNull()
	}

	fun isVmRunning(vm: VirtualMachine): Boolean {
		val dyn = vmDyns[vm.id]
		return dyn != null && dyn.status == VirtualMachineStatus.Up
	}

	fun vmHost(vm: VirtualMachine): Host? {
		return vmHost(vm.id)
	}

	fun vmHost(vmId: UUID): Host? {
		val dyn = vmDyns[vmId]
		return if (dyn == null) null else hosts[dyn.hostId]
	}

	override fun isComplete(): Boolean {
		//check that all virtual resources has all DealBreaker satisfied
		return vmsToCheck().all {
			vm ->
			vm.expectations.all {
				expectation ->
				expectation.level != ExpectationLevel.DealBreaker
						|| checkExpectation(expectation, vm)
			}
		}
	}

	fun getNrOfUnsatisfiedExpectations(level: ExpectationLevel): Int {
		return vmsToCheck().sumBy {
			vm ->
			vm.expectations.count {
				expectation ->
				expectation.level == level
						&& !checkExpectation(expectation, vm)
			}
		}
	}

	fun getUnsatisfiedExpectations(): List<Expectation> {
		var expectations = listOf<Expectation>()
		vmsToCheck()
				.forEach {
					vm ->
					expectations +=
							vm.expectations.filter {
								expectation ->
								!checkExpectation(expectation, vm)
							}
				}
		return expectations
	}

	private fun vmsToCheck(): List<VirtualMachine> {
		return vms.values
				.filterNot {
					reservations.contains(VmReservation(it))
				}
	}

	private fun checkExpectation(expectation: Expectation, vm: VirtualMachine): Boolean {
		when (expectation) {
			is ClockFrequencyExpectation             -> {
				val host = vmHost(vm)
				return if (host == null) {
					true
				} else {
					host.capabilities?.cpus?.firstOrNull()?.maxSpeedMhz ?: 0 >= expectation.minimalClockFrequency
				}
			}
			is NotSameHostExpectation                -> {
				val host = vmHost(vm)
				return if (host == null) {
					true
				} else {
					val otherVmHosts = expectation.otherVmIds.map { vmHost(it)?.id }
					!otherVmHosts.contains(host.id)
				}
			}
			is ChassisManufacturerExpectation        -> {
				val host = vmHost(vm)
				return if (host == null) {
					true
				} else {
					host.capabilities?.chassis?.manufacturer == expectation.manufacturer
				}
			}
			is CacheSizeExpectation                  -> {
				val host = vmHost(vm)
				return if (host == null) {
					true
				} else {
					expectation.minL1 <= host.capabilities?.cpus?.firstOrNull()?.l1cache?.size ?: 0
				}
			}
			is VirtualMachineAvailabilityExpectation ->
				return isVmRunning(vm) == expectation.up
			is CpuArchitectureExpectation            -> {
				val host = vmHost(vm)
				return if (host == null) {
					true
				} else {
					expectation.cpuArchitecture == host.capabilities?.cpuArchitecture
				}
			}
			is MemoryClockFrequencyExpectation -> {
				val host = vmHost(vm)
				val memoryDevices = host?.capabilities?.memoryDevices
				return memoryDevices?.isNotEmpty() ?: false
						&& memoryDevices?.all { it.speedMhz ?: 0 >= expectation.min } ?: false
			}
			else                                     ->
				throw IllegalArgumentException("Expectation ${expectation} can not be checked")
		}
	}
}