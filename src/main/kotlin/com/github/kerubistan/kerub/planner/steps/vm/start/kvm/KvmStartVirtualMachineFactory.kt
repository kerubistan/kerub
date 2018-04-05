package com.github.kerubistan.kerub.planner.steps.vm.start.kvm

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.vm.match
import com.github.kerubistan.kerub.planner.steps.vm.start.AbstractStartVmFactory
import com.github.kerubistan.kerub.planner.steps.vm.storageAllocationMap
import com.github.kerubistan.kerub.utils.join
import com.github.kerubistan.kerub.utils.junix.virt.virsh.LibvirtCapabilities
import com.github.kerubistan.kerub.utils.junix.virt.virsh.Virsh

object KvmStartVirtualMachineFactory : AbstractStartVmFactory<KvmStartVirtualMachine>() {

	override fun produce(state: OperationalState): List<KvmStartVirtualMachine> =
			getVmsToStart(state).map {
				vm ->
				getWorkingHosts(state) {
					hostData ->
					hostData.stat.capabilities?.os == OperatingSystem.Linux
							&& isHwVirtualizationSupported(hostData.stat)
							&& isKvmInstalled(hostData.stat)
							&& isKvmCapable(hostData.stat.capabilities.hypervisorCapabilities, vm)
							&& match(hostData, vm, storageAllocationMap(state, vm.virtualStorageLinks))
				}.map {
					KvmStartVirtualMachine(vm, it.stat)
				}
			}.join()

	internal fun isKvmCapable(hypervisorCapabilities: List<Any>, vm: VirtualMachine): Boolean {
		return hypervisorCapabilities.any {
			it is LibvirtCapabilities && it.guests.any { it.arch.name == vm.architecture }
		}
	}

	internal fun isKvmInstalled(host: Host)
			= Virsh.available(host.capabilities)
			&& host.capabilities?.installedSoftware?.any { it.name == "qemu-kvm" } ?: false

}