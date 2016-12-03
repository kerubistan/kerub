package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.search.annotations.Field
import java.io.Serializable
import java.util.UUID

data class AssetOwner(
		val ownerId: UUID,
		val ownerType: AssetOwnerType
) : Serializable {
	val ownerIdStr: String?
		@Field
		@JsonIgnore
		get() = "${ownerType}:${ownerId}"

}