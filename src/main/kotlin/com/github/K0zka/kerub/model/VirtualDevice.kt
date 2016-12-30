package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.K0zka.kerub.model.devices.NetworkDevice
import com.github.K0zka.kerub.model.devices.WatchdogDevice

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(NetworkDevice::class),
		JsonSubTypes.Type(WatchdogDevice::class)
)
interface VirtualDevice {
}