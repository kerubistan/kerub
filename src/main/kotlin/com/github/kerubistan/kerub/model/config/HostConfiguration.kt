package com.github.kerubistan.kerub.model.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.index.Indexed
import com.github.kerubistan.kerub.model.services.HostService
import io.github.kerubistan.kroki.delegates.weak
import java.util.UUID

@JsonTypeName("host-configuration")
data class HostConfiguration(
		override val id: UUID = UUID.randomUUID(),
		val services: List<HostService> = listOf(),
		val storageConfiguration: List<HostStorageConfiguration> = listOf(),
		val publicKey : String? = null,
		val acceptedPublicKeys: List<String> = listOf(),
		val networkConfiguration: List<HostNetworkConfiguration> = listOf()
) : Entity<UUID>, Indexed<HostConfigurationIndex> {
	@get:JsonIgnore
	override val index: HostConfigurationIndex by weak { HostConfigurationIndex(this) }
}