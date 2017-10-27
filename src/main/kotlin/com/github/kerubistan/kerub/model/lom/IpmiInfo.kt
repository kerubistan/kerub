package com.github.kerubistan.kerub.model.lom

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("ipmi")
data class IpmiInfo(
		val address: String,
		val username: String,
		val password: String
) : PowerManagementInfo