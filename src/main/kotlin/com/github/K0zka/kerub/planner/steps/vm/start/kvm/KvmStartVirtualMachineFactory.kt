package com.github.K0zka.kerub.planner.steps.vm.start.kvm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vm.match
import com.github.K0zka.kerub.planner.steps.vm.start.AbstractStartVmFactory
import com.github.K0zka.kerub.planner.steps.vm.storageAllocationMap
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.join

object KvmStartVirtualMachineFactory : AbstractStartVmFactory<KvmStartVirtualMachine>() {

	override fun produce(state: OperationalState): List<KvmStartVirtualMachine> =
			getVmsToStart(state).map {
				vm ->
				getWorkingHosts(state) {
					host, dyn ->
					host.capabilities?.os == OperatingSystem.Linux
							&& isKvmInstalled(host)
							&& match(host, dyn, vm, storageAllocationMap(state))
				}.map {
					KvmStartVirtualMachine(vm, it.key)
				}
			}.join()

	fun isKvmInstalled(host: Host)
			= host.capabilities?.installedSoftware?.any { it.name == "libvirt" } ?: false
			&& host.capabilities?.installedSoftware?.any { it.name == "qemu-kvm" } ?: false

}