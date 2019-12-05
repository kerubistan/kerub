package com.github.kerubistan.kerub.planner.issues.problems.vnet

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVirtualNetwork

internal class VirtualNetworkPartitionedTest : AbstractDataRepresentationTest<VirtualNetworkPartitioned>() {
	override val testInstances: Collection<VirtualNetworkPartitioned>
		get() = listOf(VirtualNetworkPartitioned(virtualNetwork = testVirtualNetwork, isolatedHost = testHost))
	override val clazz: Class<VirtualNetworkPartitioned>
		get() = VirtualNetworkPartitioned::class.java
}