package com.github.kerubistan.kerub.model.devices

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.VirtualDevice
import java.util.UUID

@JsonTypeName("nic")
data class NetworkDevice(
		val networkId: UUID,
		val adapterType: NetworkAdapterType
) : VirtualDevice