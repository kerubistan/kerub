package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import java.math.BigInteger

@JsonTypeName("lvm")
class LvmStorageCapability(
		val volumeGroupName: String,
		override val size: BigInteger,
		val physicalVolumes: List<BigInteger>
) : StorageCapability()