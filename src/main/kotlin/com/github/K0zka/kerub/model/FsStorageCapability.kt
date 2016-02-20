package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import java.math.BigInteger

@JsonTypeName("fs")
data class FsStorageCapability(
		override val size: BigInteger,
		val mountPoint: String
) : StorageCapability