package com.github.K0zka.kerub.model

import java.io.Serializable
import java.math.BigInteger

/**
 * Describes usage limits of a user in an account or project.
 */
data class Quota(
		val nrOfVms: Int?,
		val nrOfCpus: Int?,
		val nrOfVdisks: Int?,
		val nrOfVNets: Int?,
		val totalDiskSpace: BigInteger?,
		val totalCpuTime: Long?,
		val totalMemory: BigInteger?,
		val maxVmMemory: BigInteger?
) : Serializable