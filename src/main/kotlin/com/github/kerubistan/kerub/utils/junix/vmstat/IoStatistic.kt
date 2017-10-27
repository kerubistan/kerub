package com.github.kerubistan.kerub.utils.junix.vmstat

import java.io.Serializable

data class IoStatistic(
		val read: Int,
		val write: Int
) : Serializable