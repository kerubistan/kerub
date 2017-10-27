package com.github.kerubistan.kerub.host

import com.github.kerubistan.kerub.hypervisor.Hypervisor
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.lom.PowerManagementInfo
import java.security.PublicKey

/**
 * Host manager have to keep track of all hosts connected to the controller.
 *
 */
interface HostManager {
	/**
	 * Get the public key of a host by it's address
	 */
	fun getHostPublicKey(address: String): PublicKey

	/**
	 * Connect to the host, start monitoring processes.
	 */
	fun connectHost(host: Host)

	fun disconnectHost(host: Host)
	fun powerDown(host: Host)
	fun join(host: Host, password: String, powerManagers: List<PowerManagementInfo> = listOf()): Host
	fun join(host: Host, powerManagers: List<PowerManagementInfo> = listOf()): Host
	fun getHypervisor(host: Host): Hypervisor?
	fun getFireWall(host: Host): FireWall
	fun getServiceManager(host: Host): ServiceManager
}
