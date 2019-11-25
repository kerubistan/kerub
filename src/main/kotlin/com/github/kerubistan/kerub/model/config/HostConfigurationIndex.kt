package com.github.kerubistan.kerub.model.config

import java.io.Serializable

data class HostConfigurationIndex(private val indexOf: HostConfiguration) : Serializable {

	//------------
	// network
	//------------

	val ovsNetworkConfigurations by lazy {
		indexOf.networkConfiguration.filterIsInstance<OvsNetworkConfiguration>().associateBy { it.virtualNetworkId }
	}

}