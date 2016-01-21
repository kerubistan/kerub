package com.github.K0zka.kerub.model.dynamic

import java.io.Serializable
import java.util.UUID

interface VirtualStorageAllocation : Serializable {
	val hostId : UUID
}