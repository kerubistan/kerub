package com.github.kerubistan.kerub.planner.issues.problems.storage

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testLvmCapability
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB

class LvmPoolAlmostFullTest : AbstractDataRepresentationTest<LvmPoolAlmostFull>() {
	override val testInstances = listOf(LvmPoolAlmostFull(host = testHost, freeSpace = 100.GB, pool = LvmPoolConfiguration(vgName = testLvmCapability.volumeGroupName, poolName = "pool-1", size = 40.TB)))
	override val clazz = LvmPoolAlmostFull::class.java

}