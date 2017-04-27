package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.Host
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
	fun join(host: Host, password: String): Host
	fun join(host: Host): Host
	fun getHypervisor(host: Host): Hypervisor?
	fun getFireWall(host : Host) : FireWall
	fun getServiceManager(host: Host) : ServiceManager
}
