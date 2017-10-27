package com.github.kerubistan.kerub.hypervisor

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.display.RemoteConsoleProtocol

/**
 * Hypervisor of a host.
 * A hypervisor represents an installation of a hypervisor software on a host, therefore it implementations
 * should have connection to the host.
 */
interface Hypervisor {
	fun startVm(vm: VirtualMachine, consolePwd: String)
	fun getDisplay(vm: VirtualMachine): Pair<RemoteConsoleProtocol, Int>
	fun stopVm(vm: VirtualMachine)
	fun migrate(vm: VirtualMachine, source: Host, target: Host)
	fun suspend(vm: VirtualMachine)
	fun resume(vm: VirtualMachine)
	/**
	 * Start monitoring process for the VM's
	 */
	fun startMonitoringProcess()
}