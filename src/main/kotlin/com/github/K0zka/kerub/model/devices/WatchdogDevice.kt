package com.github.K0zka.kerub.model.devices

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.VirtualDevice

@JsonTypeName("network-device")
data class WatchdogDevice(
		val type: WatchdogType,
		val action: WatchdogAction
) : VirtualDevice