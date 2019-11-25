package com.github.kerubistan.kerub.model.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.kerubistan.kroki.delegates.weak
import java.io.Serializable
import java.util.UUID

@JsonTypeName("ovs-network-config")
data class OvsNetworkConfiguration(
		override val virtualNetworkId: UUID,
		val ports: List<OvsPort> = listOf()
) : HostNetworkConfiguration, Serializable {
	@get:JsonIgnore
	val index by weak { OvsNetworkConfigurationIndex(this) }
}