package com.github.kerubistan.kerub.planner.steps.vm.start.kvm

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.hypervisor.LibvirtCapabilities
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.vm.allStorageAvailable
import com.github.kerubistan.kerub.planner.steps.vm.match
import com.github.kerubistan.kerub.planner.steps.vm.start.AbstractStartVmFactory
import com.github.kerubistan.kerub.planner.steps.vm.virtualStorageLinkInfo
import com.github.kerubistan.kerub.utils.junix.common.anyPackageNamed
import com.github.kerubistan.kerub.utils.junix.virt.virsh.Virsh
import io.github.kerubistan.kroki.collections.join
import kotlin.reflect.KClass

object KvmStartVirtualMachineFactory : AbstractStartVmFactory<KvmStartVirtualMachine>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<KvmStartVirtualMachine> =
			getVmsToStart(state).map { vm ->
				state.index.runningHosts.mapNotNull { hostData ->
					val virtualStorageLinks = lazy {
						virtualStorageLinkInfo(
								state = state,
								links = vm.virtualStorageLinks,
								targetHostId = hostData.stat.id)
					}
					if (isHostKvmReady(hostData)
							&& isHostCapableOfVm(hostData, vm)
							&& allStorageAvailable(vm, virtualStorageLinks.value)
					) {
						KvmStartVirtualMachine(
								vm = vm,
								host = hostData.stat,
								storageLinks = virtualStorageLinks.value)

					} else null
				}
			}.join()

	private fun isHostKvmReady(hostData: HostDataCollection): Boolean =
			(hostData.stat.capabilities?.os == OperatingSystem.Linux
					&& isHwVirtualizationSupported(hostData.stat)
					&& isKvmInstalled(hostData.stat))

	private fun isHostCapableOfVm(hostData: HostDataCollection, vm: VirtualMachine) =
			isKvmCapableVmArch(hostData.stat.capabilities?.hypervisorCapabilities ?: listOf(), vm)
					&& match(hostData, vm)

	internal fun isKvmCapableVmArch(hypervisorCapabilities: List<Any>, vm: VirtualMachine): Boolean =
			hypervisorCapabilities.any {
				it is LibvirtCapabilities && it.guests.any { it.arch.name == vm.architecture }
			}

	internal fun isKvmInstalled(host: Host) = Virsh.available(host.capabilities)
			&& host.capabilities.anyPackageNamed("qemu-kvm")

}