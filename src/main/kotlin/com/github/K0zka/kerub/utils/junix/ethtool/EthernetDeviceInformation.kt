package com.github.K0zka.kerub.utils.junix.ethtool

import java.math.BigInteger

data class EthernetDeviceInformation(
		val wakeOnLan: Boolean,
		val transferRate: BigInteger,
		val link: Boolean
)