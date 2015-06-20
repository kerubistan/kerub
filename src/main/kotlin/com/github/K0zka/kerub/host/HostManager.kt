package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.model.Host
import java.security.PublicKey

/**
 * Host manager have to keep track of all hosts connected to the controller.
 *
 */
public interface HostManager {
	fun registerHost()
	fun getHostPublicKey(address : String) : PublicKey
	fun connectHost(host : Host)
}
