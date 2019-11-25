package com.github.kerubistan.kerub.utils.junix.ovs

import java.util.UUID

data class OvsPort(
		override val id: UUID,
		val name: String
) : OvsRecord