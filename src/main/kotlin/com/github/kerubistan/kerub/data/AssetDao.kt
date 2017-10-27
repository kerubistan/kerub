package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.Asset
import com.github.kerubistan.kerub.model.AssetOwner
import java.util.UUID

/**
 *
 */
interface AssetDao<T : Asset> :
		DaoOperations.SimpleSearch<T>,
		ListableCrudDao<T, UUID>,
		DaoOperations.ByName<Asset> {

	/**
	 * Count the entities where the owner is one of the given
	 */
	fun count(owners: Set<AssetOwner>): Int


	fun listByOwners(
			owners: Collection<AssetOwner>,
			start: Long = 0,
			limit: Int = Int.MAX_VALUE,
			sort: String = "id"): List<T>

	fun listByOwner(
			owner: AssetOwner,
			start: Long = 0,
			limit: Int = Int.MAX_VALUE,
			sort: String = "id"): List<T>

	fun fieldSearch(
			owners: Set<AssetOwner>,
			field: String,
			value: String,
			start: Long = 0,
			limit: Int = Int.MAX_VALUE
	): List<T>

	fun getByName(
			owner: AssetOwner,
			name: String
	): List<T>

}