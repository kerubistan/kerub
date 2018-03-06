package com.github.kerubistan.kerub.hypervisor.virtualbox

import com.github.kerubistan.kerub.hypervisor.Hypervisor
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.display.RemoteConsoleProtocol
import com.github.kerubistan.kerub.utils.junix.virt.vbox.VBoxManage
import org.apache.sshd.client.session.ClientSession

class VirtualBoxHypervisor(private val client: ClientSession) : Hypervisor {
	override fun startVm(vm: VirtualMachine, consolePwd: String) {
		TODO()
	}

	override fun getDisplay(vm: VirtualMachine): Pair<RemoteConsoleProtocol, Int> {
		TODO()
	}

	override fun stopVm(vm: VirtualMachine) {
		VBoxManage.stopVm(session = client, vm = vm)
	}

	override fun migrate(vm: VirtualMachine, source: Host, target: Host) {
		TODO()
	}

	override fun suspend(vm: VirtualMachine) {
		 VBoxManage.pauseVm(session = client, vm = vm)
	}

	override fun resume(vm: VirtualMachine) {
		VBoxManage.resetVm(session = client, vm = vm)
	}

	override fun startMonitoringProcess() {
		TODO()
	}
}