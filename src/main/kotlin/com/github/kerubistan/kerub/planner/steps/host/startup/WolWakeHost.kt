package com.github.kerubistan.kerub.planner.steps.host.startup

import com.github.kerubistan.kerub.model.Host

data class WolWakeHost(override val host: Host) : AbstractWakeHost()