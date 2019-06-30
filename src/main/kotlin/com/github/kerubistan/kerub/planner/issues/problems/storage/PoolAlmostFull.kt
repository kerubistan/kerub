package com.github.kerubistan.kerub.planner.issues.problems.storage

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import java.math.BigInteger

data class PoolAlmostFull(
		val host: Host,
		val pool: LvmPoolConfiguration,
		val freeSpace: BigInteger
) : Problem