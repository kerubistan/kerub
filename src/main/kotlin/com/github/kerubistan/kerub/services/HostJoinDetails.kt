package com.github.kerubistan.kerub.services

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.lom.PowerManagementInfo

data class HostJoinDetails(
		@JsonProperty("host")
		val host: Host,
		@JsonProperty("powerManagement")
		val powerManagement: List<PowerManagementInfo> = listOf()
)