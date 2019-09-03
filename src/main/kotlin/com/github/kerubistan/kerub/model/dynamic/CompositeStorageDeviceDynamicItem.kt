package com.github.kerubistan.kerub.model.dynamic

import java.io.Serializable
import java.math.BigInteger

interface CompositeStorageDeviceDynamicItem : Serializable {
	val name : String
	val freeCapacity: BigInteger
}