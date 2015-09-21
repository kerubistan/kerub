package com.github.K0zka.kerub.hypervisor.kvm

import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.host.use
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import org.apache.sshd.ClientSession

class KvmHypervisor(val client: ClientSession) : Hypervisor {
	override fun startVm(vm: VirtualMachine) {
		val domainDef = "/tmp/${vm.id}.xml"
		client.createSftpClient().use {
			it.write(domainDef).use { it.write(vmDefinitiontoXml(vm).toByteArray("UTF-8")) }
		}
		client.createExecChannel("virsh create ${domainDef}")
	}

	override fun stopVm(vm: VirtualMachine) {
		client.execute("virsh destroy ${vm.id} --graceful")
	}

	override fun migrate(vm: VirtualMachine, target: Host) {
		throw UnsupportedOperationException()
	}

}