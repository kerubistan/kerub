package com.github.K0zka.kerub.hypervisor.kvm

import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.utils.junix.virt.virsh.Virsh
import org.apache.sshd.ClientSession

class KvmHypervisor(private val client: ClientSession) : Hypervisor {
	override fun suspend(vm: VirtualMachine) {
		Virsh.suspend(client, vm.id)
	}

	override fun resume(vm: VirtualMachine) {
		Virsh.resume(client, vm.id)
	}

	override fun startVm(vm: VirtualMachine) {
		Virsh.create(client, vm.id, vmDefinitiontoXml(vm))
	}

	override fun stopVm(vm: VirtualMachine) {
		Virsh.destroy(client, vm.id)
	}

	override fun migrate(vm: VirtualMachine, source: Host, target: Host) {
		throw UnsupportedOperationException()
	}

}