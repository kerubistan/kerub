package com.github.K0zka.kerub.model

import java.io.Serializable
import java.math.BigInteger

abstract class StorageCapability : Serializable {
		abstract val size: BigInteger
}