package com.github.K0zka.kerub.services

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.lom.PowerManagementInfo

data class HostAndPassword @JsonCreator constructor(
		@JsonProperty("host")
		val host: Host,
		@JsonProperty("powerManagement")
		val powerManagement: List<PowerManagementInfo> = listOf(),
		@JsonProperty("password")
		val password: String
)