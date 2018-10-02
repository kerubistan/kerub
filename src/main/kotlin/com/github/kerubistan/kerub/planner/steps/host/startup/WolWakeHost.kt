package com.github.kerubistan.kerub.planner.steps.host.startup

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.steps.ProducedBy

@ProducedBy(WakeHostFactory::class)
data class WolWakeHost(override val host: Host) : AbstractWakeHost()