package com.github.K0zka.kerub.model.hardware

import java.io.Serializable


data class CacheInformation (
		val socket: String,
		val operation: String,
		val sizeKb: Int,
		val speedNs: Int?,
		val errorCorrection: String
                            ) : Serializable