package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.model.*
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.model.expectations.CpuArchitectureExpectation
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.k0zka.finder4j.backtrack.State
import java.util.UUID

data class OperationalState(
		val hosts: Map<UUID, Host> = mapOf(),
		val hostDyns: Map<UUID, HostDynamic> = mapOf(),
		val vms: Map<UUID, VirtualMachine> = mapOf(),
		val vmDyns: Map<UUID, VirtualMachineDynamic> = mapOf()
                           ) : State {

	companion object {
		fun fromLists(hosts: List<Host> = listOf(),
		              hostDyns: List<HostDynamic> = listOf(),
		              vms: List<VirtualMachine> = listOf(),
		              vmDyns: List<VirtualMachineDynamic> = listOf()): OperationalState {
			return OperationalState(
					hosts = hosts.toMap { it.id },
					hostDyns = hostDyns.toMap { it.id },
					vms = vms.toMap { it.id },
			        vmDyns = vmDyns.toMap { it.id }
			                       )
		}
	}

	fun vmsOnHost(hostId: UUID): List<VirtualMachine> {
		return vmDyns.values()
				.filter { it.status == VirtualMachineStatus.Up && it.hostId == hostId }
				.map { vms[it.id] }.filter { it != null } as List<VirtualMachine>
	}

	fun isVmRunning(vm: VirtualMachine): Boolean {
		val dyn = vmDyns[vm.id]
		return dyn != null && dyn.status == VirtualMachineStatus.Up
	}

	fun vmHost(vm: VirtualMachine): Host? {
		val dyn = vmDyns[vm.id]
		return if (dyn == null) null else hosts[dyn.hostId]
	}

	override fun isComplete(): Boolean {
		//check that all virtual resources has all DealBreaker satisfied
		return vms.values().all {
			vm ->
			vm.expectations.all {
				expectation ->
				expectation.level != ExpectationLevel.DealBreaker
						|| checkExpectation(expectation, vm)
			}
		}
	}

	fun getNrOfUnsatisfiedExpectations(level: ExpectationLevel) : Int {
		return vms.values().sumBy {
			vm ->
			vm.expectations.count {
				expectation ->
				expectation.level == level
					&& !checkExpectation(expectation, vm)
			}
		}
	}

	fun getUnsatisfiedExpectations() : List<Expectation> {
		var expectations = listOf<Expectation>()
		vms.values().forEach {
			vm ->
			expectations +=
			vm.expectations.filter {
				expectation ->
						!checkExpectation(expectation, vm)
			}
		}
		return expectations
	}

	private fun checkExpectation(expectation: Expectation, vm: VirtualMachine): Boolean {
		when (expectation) {
			is VirtualMachineAvailabilityExpectation ->
				return isVmRunning(vm) == expectation.up
			is CpuArchitectureExpectation -> {
				var host = vmHost(vm)
				return if(host == null) {
					true
				} else {
					expectation.cpuArchitecture == host.capabilities?.cpuArchitecture
				}
			}
			else ->
				throw IllegalArgumentException("Expectation ${expectation} can not be checked")
		}
	}
}