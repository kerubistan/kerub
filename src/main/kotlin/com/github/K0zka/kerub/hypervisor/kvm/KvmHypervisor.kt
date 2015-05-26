package com.github.K0zka.kerub.hypervisor.kvm

import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import org.apache.sshd.SshClient

class KvmHypervisor(val client : SshClient) : Hypervisor {
	override fun startVm(vm: VirtualMachine) {

	}

	override fun stopVm(vm: VirtualMachine) {
		throw UnsupportedOperationException()
	}

	override fun migrate(vm: VirtualMachine, target: Host) {
		throw UnsupportedOperationException()
	}

}