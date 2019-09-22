package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.utils.validateSize
import java.io.Serializable
import java.math.BigInteger

data class StoragePoolDynamic(
		val name : String,
		val size : BigInteger,
		val freeSize : BigInteger
) : Serializable {
	init {
		size.validateSize("size")
		freeSize.validateSize("freeSize")
		check(size >= freeSize) {
			"size ($size) must be greater or equal to freeSize ($freeSize)"
		}
	}
}