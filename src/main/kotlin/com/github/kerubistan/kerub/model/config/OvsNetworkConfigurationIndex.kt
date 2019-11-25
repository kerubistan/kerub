package com.github.kerubistan.kerub.model.config

import java.io.Serializable

data class OvsNetworkConfigurationIndex(private val indexOf: OvsNetworkConfiguration) : Serializable {

	val portNames by lazy {
		indexOf.ports.map { it.name }.toSet()
	}

}