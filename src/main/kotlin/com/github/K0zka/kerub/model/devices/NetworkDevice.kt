package com.github.K0zka.kerub.model.devices

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.VirtualDevice
import java.util.UUID

@JsonTypeName("network-device")
data class NetworkDevice(
		val networkId: UUID,
		val adapterType: NetworkAdapterType
) : VirtualDevice