package com.github.K0zka.kerub.planner.steps.host.startup

import com.github.K0zka.kerub.model.Host

data class IpmiWakeHost(override val host : Host) : AbstractWakeHost() {
}