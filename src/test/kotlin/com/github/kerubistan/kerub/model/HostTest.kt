package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testOtherHost

internal class HostTest : AbstractDataRepresentationTest<Host>() {
	override val testInstances = listOf(testHost, testFreeBsdHost, testOtherHost)
	override val clazz = Host::class.java
}