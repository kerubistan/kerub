package com.github.kerubistan.kerub.utils.junix.benchmarks.bonnie

import java.io.Serializable
import java.math.BigInteger

data class IoBenchmarkItem(val throughput: BigInteger, val latency: Long, val cpuUsagePercent: Short) : Serializable