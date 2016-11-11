package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Asset
import com.github.K0zka.kerub.model.AssetOwner
import java.util.UUID

interface AssetDao<T : Asset> : DaoOperations.Read<T, UUID> {
	fun listByOwners(
			owners: Collection<AssetOwner>,
			start: Long = 0,
			limit: Long = java.lang.Long.MAX_VALUE,
			sort: String = "id"): List<T>

	fun listByOwner(
			owner: AssetOwner,
			start: Long = 0,
			limit: Long = java.lang.Long.MAX_VALUE,
			sort: String = "id"): List<T>

}