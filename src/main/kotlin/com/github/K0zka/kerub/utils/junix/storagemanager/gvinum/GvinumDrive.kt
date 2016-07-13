package com.github.K0zka.kerub.utils.junix.storagemanager.gvinum

import java.io.Serializable
import java.math.BigInteger

data class GvinumDrive(
		val name : String,
		val device : String,
		val size : BigInteger,
		val used : BigInteger,
		val available : BigInteger,
		val up : Boolean
) : Serializable