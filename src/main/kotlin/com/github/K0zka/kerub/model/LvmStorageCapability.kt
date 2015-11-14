package com.github.K0zka.kerub.model

import java.math.BigInteger

class LvmStorageCapability(
		val volumeGroupName: String,
		override val size : BigInteger
) : StorageCapability()