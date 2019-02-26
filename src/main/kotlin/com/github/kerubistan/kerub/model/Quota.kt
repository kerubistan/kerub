package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.utils.validateSize
import java.io.Serializable
import java.math.BigInteger

/**
 * Describes usage limits of a user in an account or project.
 */
data class Quota(
		val nrOfVms: Int? = null,
		val nrOfCpus: Int? = null,
		val nrOfVdisks: Int? = null,
		val nrOfVNets: Int? = null,
		val totalDiskSpace: BigInteger? = null,
		val totalCpuTime: Long? = null,
		val totalMemory: BigInteger? = null,
		val maxVmMemory: BigInteger? = null
) : Serializable {
	init {
		totalDiskSpace?.validateSize("totalDiskSpace")
		totalMemory?.validateSize("totalMemory")
		maxVmMemory?.validateSize("maxVmMemory")
	}
}