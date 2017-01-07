package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.AssetDao
import com.github.K0zka.kerub.model.Asset
import com.github.K0zka.kerub.model.AssetOwner
import com.github.K0zka.kerub.model.AssetOwnerType
import com.github.K0zka.kerub.model.paging.SearchResultPage
import com.github.K0zka.kerub.model.paging.SortResultPage
import com.github.K0zka.kerub.security.AccessController
import com.github.K0zka.kerub.services.AssetService
import java.util.UUID

abstract class AbstractAssetService<T : Asset>(
		val accessController: AccessController,
		override val dao: AssetDao<T>,
		entityType: String
) : ListableBaseService<T>(entityType), AssetService<T> {

	override fun listByOwner(start: Long, limit: Int, sort: String, ownerType: AssetOwnerType, ownerId: UUID): SortResultPage<T> {
		val list = dao.listByOwner(
				owner = AssetOwner(ownerId, ownerType),
				start = start,
				limit = limit,
				sort = sort
		)
		return SortResultPage(
				start = start,
				count = list.size.toLong(),
				result = list,
				sortBy = sort,
				total = list.size.toLong() //TODO
		)
	}

	override fun search(field: String, value: String, start: Long, limit: Int, ownerType: AssetOwnerType, ownerId: UUID): SearchResultPage<T> {
		val list = dao.fieldSearch(
				setOf(AssetOwner(ownerId, ownerType)),
				field,
				value
		)
		return SearchResultPage(
				start = start,
				count = list.size.toLong(),
				result = list,
				total = list.size.toLong(), //TODO
				searchby = field
		)
	}


	override fun getById(id: UUID): T =
			assertExist(entityType, accessController.doAndCheck() {
				super.getById(id)
			}, id)

	override fun update(id: UUID, entity: T): T =
			assertExist(entityType, accessController.checkAndDo(entity) {
				super.update(id, entity)
			}, id)

	override fun delete(id: UUID) {
		val entity: T? = accessController.doAndCheck { dao[id] }

		dao.remove(assertExist(entityType, entity, id))
	}

	override fun add(entity: T): T =
			accessController.checkAndDo(asset = entity) {
				super.add(entity)
			} ?: entity

	override fun listAll(start: Long, limit: Int, sort: String): SortResultPage<T> =
			accessController.listWithFilter(dao, start, limit, sort)

	override fun getByName(name: String): List<T>
			= accessController.filter( dao.getByName(name) as List<T> )

	override fun getByNameAndOwner(ownerType: AssetOwnerType, ownerId: UUID, name: String): List<T>
			= TODO("https://github.com/kerubistan/kerub/issues/173")

}