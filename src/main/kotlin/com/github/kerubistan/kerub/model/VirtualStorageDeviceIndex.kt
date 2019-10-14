package com.github.kerubistan.kerub.model

import java.io.Serializable

data class VirtualStorageDeviceIndex(private val indexOf: VirtualStorageDevice) : Serializable {
	val expectationsByClass by lazy {
		indexOf.expectations.groupBy { it.javaClass.name }
	}
}