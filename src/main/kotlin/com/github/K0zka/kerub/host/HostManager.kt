package com.github.K0zka.kerub.host

import java.security.PublicKey

public trait HostManager {
	fun registerHost()
	fun getHostPublicKey(address : String) : PublicKey
}