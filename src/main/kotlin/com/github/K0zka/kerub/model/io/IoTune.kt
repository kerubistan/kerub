package com.github.K0zka.kerub.model.io

import java.io.Serializable

data class IoTune(
		val kbPerSec: Int?,
		val iopsPerSec: Int?
                 ) : Serializable