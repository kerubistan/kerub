package com.github.kerubistan.kerub.model.dynamic

import java.math.BigInteger

abstract class CompositeStorageDeviceDynamicItem {
	abstract val name : String
	abstract val freeCapacity: BigInteger
}