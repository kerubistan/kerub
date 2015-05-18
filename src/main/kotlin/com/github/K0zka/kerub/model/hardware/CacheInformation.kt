package com.github.K0zka.kerub.model.hardware


data class CacheInformation (
		val socket: String,
		val operation: String,
		val sizeKb: Int,
		val speedNs: Int?,
		val errorCorrection: String
                            )