package com.github.kerubistan.kerub.utils.junix.procfs

data class BondingInfo (
		val mode : BondingMode,
		val slaves : List<SlaveInterface>
)