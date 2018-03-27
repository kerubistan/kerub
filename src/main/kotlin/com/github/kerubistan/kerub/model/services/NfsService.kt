package com.github.kerubistan.kerub.model.services

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("nfs-share")
data class NfsService(
		val directory: String
) : HostService