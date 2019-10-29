package com.github.kerubistan.kerub.planner.steps.host.startup

import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testHost

internal class WolWakeHostTest : OperationalStepVerifications() {
	override val step = WolWakeHost(host = testHost)
}