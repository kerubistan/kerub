package com.github.K0zka.kerub.utils.junix.virt.vbox

import java.io.Serializable
import java.math.BigInteger

data class VBoxMetrics(
		val userCpu: VBoxMetric<Float>,
		val kernelCpu: VBoxMetric<Float>,
		val ramUsed: VBoxMetric<BigInteger>,
		val diskUsed: VBoxMetric<BigInteger>,
		val netRateRx: VBoxMetric<Int>,
		val netRateTx: VBoxMetric<Int>
) : Serializable