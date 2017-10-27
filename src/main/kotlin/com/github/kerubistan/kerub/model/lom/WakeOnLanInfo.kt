package com.github.kerubistan.kerub.model.lom

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("wake-on-lan")
data class WakeOnLanInfo(
		val macAddresses: List<ByteArray> = listOf()
) : PowerManagementInfo