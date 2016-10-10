package com.github.K0zka.kerub.utils.junix.zfs

import java.io.Serializable
import java.math.BigInteger

data class ZfsObject(
		val type: ZfsObjectType,
		val name : String,
		val used : BigInteger,
		val usedByDataset : BigInteger,
		val usedbychildren : BigInteger

) : Serializable