package com.github.kerubistan.kerub.utils.junix.virt.vbox

import java.io.Serializable

data class VBoxMetric<T>(
		val now: T,
		val min: T,
		val max: T,
		val avg: T
) : Serializable