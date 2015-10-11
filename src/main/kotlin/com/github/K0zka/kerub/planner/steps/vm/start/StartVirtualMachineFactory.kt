package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.expectations.CpuArchitectureExpectation
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.K0zka.kerub.utils.getLogger
import java.math.BigInteger
import kotlin.math.div

public object StartVirtualMachineFactory : AbstractOperationalStepFactory<StartVirtualMachine>() {

	val logger = getLogger(StartVirtualMachineFactory::class)

	override fun produce(state: OperationalState): List<StartVirtualMachine> {
		val vmsToRun = state.vms.values().filter {
			it.expectations.any {
				it is VirtualMachineAvailabilityExpectation
						&& it.up
			}
		}
		logger.debug("vms to run: {}", vmsToRun)
		val vmsActuallyRunning = state.vmDyns.values().filter { it.status == VirtualMachineStatus.Up }.map { it.id }
		logger.debug("vms running: {}", vmsActuallyRunning)
		val vmsToStart = vmsToRun.filter { !vmsActuallyRunning.contains(it.id) }

		logger.debug("vms to start: {}", vmsToStart)

		var steps = listOf<StartVirtualMachine>()

		vmsToStart.forEach {
			vm ->
			state.hosts.values().forEach {
				host ->
				val dyn = state.hostDyns[host.id]
				if (match(host, dyn, vm)) {
					steps += StartVirtualMachine(vm, host)
				}
			}
		}

		logger.debug("steps generated: {}", steps)

		return steps
	}

	val mb = 1024 * 1024

	fun match(host: Host, dyn: HostDynamic?, vm: VirtualMachine): Boolean {

		if(host.capabilities?.cpus?.map { it.coreCount ?: 1 } ?.sum() ?: 1 < vm.nrOfCpus) {
			return false
		}

		if((host.capabilities?.totalMemory?: BigInteger.ZERO) < vm.memory.min) {
			return false
		}

		val cpuArchitectureExpectation = vm.expectations
				.firstOrNull { it is CpuArchitectureExpectation } as CpuArchitectureExpectation?

		logger.debug("match host {} for vm {}", host, vm)

		return dyn?.status == HostStatus.Up &&
				(cpuArchitectureExpectation?.cpuArchitecture == null
						|| host.capabilities?.cpuArchitecture == cpuArchitectureExpectation?.cpuArchitecture)

	}
}