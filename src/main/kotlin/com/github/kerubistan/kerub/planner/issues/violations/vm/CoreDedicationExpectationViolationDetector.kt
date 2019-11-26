package com.github.kerubistan.kerub.planner.issues.violations.vm

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.collection.VirtualMachineDataCollection
import com.github.kerubistan.kerub.model.expectations.CoreDedicationExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.utils.any
import io.github.kerubistan.kroki.collections.concat

object CoreDedicationExpectationViolationDetector : AbstractVmHostViolationDetector<CoreDedicationExpectation>() {
	override fun checkWithHost(
			entity: VirtualMachine,
			expectation: CoreDedicationExpectation,
			state: OperationalState,
			host: Host
	): Boolean {
		val vmsOnHost = lazy { state.vmDataOnHost(host.id) }
		val hostCoreCnt = lazy { host.capabilities?.cpus?.sumBy { it.coreCount ?: 0 } ?: 0 }
		val coredDedicated: (VirtualMachineDataCollection) -> Boolean =
				{ it.stat.expectations.any<CoreDedicationExpectation>() }
		val vmNrOfCpus: (VirtualMachineDataCollection) -> Int = { it.stat.nrOfCpus }

		// if this vm has CPU affinity to a smaller nr of cores, than the number of vcpus, that
		// means this expectation is not met... however I would say that may be true even with
		// non-dedicated vcpus
		// to be on the safe side, let's check and break this expectation if so
		return entity.nrOfCpus <= requireNotNull(state.vms[entity.id]).dynamic?.coreAffinity?.size ?: hostCoreCnt.value
				&&
				isUnderUtilized(vmsOnHost, vmNrOfCpus, hostCoreCnt)
				||
				isSafelyOverUtilized(vmsOnHost, coredDedicated, vmNrOfCpus, hostCoreCnt)
	}

	/**
	 * over-allocation
	 * the vm's without core-dedication are stick to a number of cores
	 * so that the ones with core-dedication have enough cores left
	 * @param vmsOnHost list of virtual machines running on the host
	 */
	private fun isSafelyOverUtilized(
			vmsOnHost: Lazy<List<VirtualMachineDataCollection>>,
			coredDedicated: (VirtualMachineDataCollection) -> Boolean,
			vmNrOfCpus: (VirtualMachineDataCollection) -> Int,
			hostCoreCnt: Lazy<Int>
	): Boolean {

		val vmsByDedication = vmsOnHost.value.groupBy(coredDedicated)
		val dedicatedCoreVms = vmsByDedication[true] ?: listOf()
		val notDedicatedCoreVms = vmsByDedication[false] ?: listOf()
		val allCpus = lazy { (1..hostCoreCnt.value).toList() }

		return notDedicatedCoreVms
				.map { it.dynamic?.coreAffinity ?: allCpus.value }
				.concat().toSet().size +
				dedicatedCoreVms
						.sumBy(vmNrOfCpus) < hostCoreCnt.value
	}

	/**
	 * under-utilization
	 * the total of vcpus on the server is less (or equal if this is the only vm on host)
	 * to the cores in the host -> no further enforcement needed, it is fine
	 */
	private fun isUnderUtilized(
			vmsOnHost: Lazy<List<VirtualMachineDataCollection>>,
			vmNrOfCpus: (VirtualMachineDataCollection) -> Int,
			hostCoreCnt: Lazy<Int>
	) = vmsOnHost.value.sumBy(vmNrOfCpus) <= hostCoreCnt.value

}