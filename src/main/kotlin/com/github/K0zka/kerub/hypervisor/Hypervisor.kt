package com.github.K0zka.kerub.hypervisor

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine

/**
 * Hypervisor of a host.
 * A hypervisor represents an installation of a hypervisor software on a host, therefore it implementations
 * should have connection to the host.
 */
public interface Hypervisor {
	fun startVm(vm : VirtualMachine)
	fun stopVm(vm : VirtualMachine)
	fun migrate(vm: VirtualMachine, source: Host, target: Host)
	fun suspend(vm: VirtualMachine)
	fun resume(vm: VirtualMachine)
	/**
	 * Start monitoring process for the VM's
	 */
	fun startMonitoringProcess()
}