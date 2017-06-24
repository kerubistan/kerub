package com.github.K0zka.kerub.utils.junix.ifconfig

import java.io.Serializable

data class NetInterface(
		val name: String,
		val mtu: Int,
		val inet6Addr: String? = null,
		val inet4Addr: String? = null,
		val mac: String? = null,
		val recPackets: Long? = null,
		val recBytes: Long? = null,
		val recErrors: Int? = null,
		val sendPackets: Long? = null,
		val sendBytes: Long? = null,
		val sendErrors: Int? = null
) : Serializable