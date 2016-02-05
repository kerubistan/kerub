package com.github.K0zka.kerub.model

import java.math.BigInteger

data class FsStorageCapability(
		override val size: BigInteger,
		val mountPoint: String
) : StorageCapability