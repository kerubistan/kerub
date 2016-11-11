package com.github.K0zka.kerub.model

import java.io.Serializable
import java.util.UUID

data class AssetOwner(
		val ownerId: UUID,
		val ownerType: AssetOwnerType
) : Serializable