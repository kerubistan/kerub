package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.kerubistan.kerub.model.devices.NetworkDevice
import com.github.kerubistan.kerub.model.devices.WatchdogDevice
import java.io.Serializable

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(NetworkDevice::class),
		JsonSubTypes.Type(WatchdogDevice::class)
)
interface VirtualDevice : Serializable