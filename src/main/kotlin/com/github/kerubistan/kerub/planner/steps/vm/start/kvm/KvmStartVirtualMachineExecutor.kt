package com.github.kerubistan.kerub.planner.steps.vm.start.kvm

import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.hypervisor.kvm.vmDefinitiontoXml
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.DisplaySettings
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.genPassword
import com.github.kerubistan.kerub.utils.junix.virt.virsh.SecretType
import com.github.kerubistan.kerub.utils.junix.virt.virsh.Virsh
import com.github.kerubistan.kerub.utils.kick
import java.math.BigInteger

class KvmStartVirtualMachineExecutor(
		private val hostManager: HostManager,
		private val vmDynDao: VirtualMachineDynamicDao,
		private val hostCommandExecutor: HostCommandExecutor
) : AbstractStepExecutor<KvmStartVirtualMachine, DisplaySettings?>() {

	override fun update(step: KvmStartVirtualMachine, updates: DisplaySettings?) {
		val dyn = VirtualMachineDynamic(
				id = step.vm.id,
				displaySetting = updates,
				hostId = step.host.id,
				status = VirtualMachineStatus.Up,
				memoryUsed = BigInteger.ZERO
		)
		vmDynDao.update(dyn)
	}

	override fun perform(step: KvmStartVirtualMachine): DisplaySettings? =
			hostCommandExecutor.execute(step.host) { client ->
				val consolePwd = genPassword(length = 16)

				step.storageLinks.forEach { storageLink ->
					when (storageLink.hostServiceUsed) {
						is IscsiService ->
							if (storageLink.hostServiceUsed.password != null) {
								Virsh.setSecret(
										client,
										storageLink.hostServiceUsed.vstorageId,
										SecretType.iscsi,
										storageLink.hostServiceUsed.password)
							}
					}
				}
				Virsh.create(client, step.vm.id, vmDefinitiontoXml(step.vm, step.storageLinks, consolePwd, step.host))
				val display = kick(8) {
					// why kicking it again? it does happen sometimes that this fails after successful vm start
					// (noticed on opensuse 42)
					Virsh.getDisplay(session = client, vmId = step.vm.id)
				}

				display?.let {
					hostManager.getFireWall(step.host).open(it.second, "tcp")

					DisplaySettings(
							hostAddr = step.host.address,
							password = consolePwd,
							ca = "",
							port = it.second
					)
				}
			}
}