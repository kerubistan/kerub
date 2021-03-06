package com.github.kerubistan.kerub.planner.steps.host.startup

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.steps.ProducedBy

@ProducedBy(WakeHostFactory::class)
@JsonTypeName("host-wol-wake")
data class WolWakeHost(override val host: Host) : AbstractWakeHost()