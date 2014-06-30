package com.github.K0zka.kerub.host

import java.security.PublicKey
import com.github.K0zka.kerub.model.Host

public trait HostManager {
	fun registerHost()
	fun getHostPublicKey(address : String) : PublicKey
	fun connectHost(host : Host)
}
