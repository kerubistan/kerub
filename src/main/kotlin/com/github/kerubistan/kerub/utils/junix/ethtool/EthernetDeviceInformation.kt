package com.github.kerubistan.kerub.utils.junix.ethtool

import java.math.BigInteger

data class EthernetDeviceInformation(
		val wakeOnLan: Boolean,
		val transferRate: BigInteger,
		val link: Boolean
)