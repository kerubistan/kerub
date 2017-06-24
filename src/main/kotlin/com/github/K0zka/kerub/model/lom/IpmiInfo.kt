package com.github.K0zka.kerub.model.lom

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("ipmi")
data class IpmiInfo(
		val address: String,
		val username: String,
		val password: String
) : PowerManagementInfo