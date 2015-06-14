package com.github.K0zka.kerub.host

import java.security.PublicKey
import com.github.K0zka.kerub.model.Host

/**
 * Host manager have to keep track of all hosts connected to the controller.
 *
 */
public interface HostManager {
	fun registerHost()
	fun getHostPublicKey(address : String) : PublicKey
	fun connectHost(host : Host)
}
