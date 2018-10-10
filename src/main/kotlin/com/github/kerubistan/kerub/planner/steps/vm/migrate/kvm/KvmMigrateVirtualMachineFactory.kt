package com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.vm.match
import com.github.kerubistan.kerub.utils.join

/**
 * Takes each running virtual machines and running hosts except the one the VM is running on
 * and generates steps if the host matches the requirements of the VM.
 */
object KvmMigrateVirtualMachineFactory : AbstractOperationalStepFactory<KvmMigrateVirtualMachine>() {
	override fun produce(state: OperationalState): List<KvmMigrateVirtualMachine> =
			state.runningHosts.map { hostData ->
				state.runningVms.mapNotNull { vmData ->
					if (match(hostData, vmData.stat)) {
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
			}.join()

}