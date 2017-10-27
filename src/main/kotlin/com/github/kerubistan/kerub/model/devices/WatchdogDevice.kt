package com.github.kerubistan.kerub.model.devices

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.VirtualDevice

@JsonTypeName("watchdog")
data class WatchdogDevice(
		val type: WatchdogType,
		val action: WatchdogAction
) : VirtualDevice