package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.AssetDao
import com.github.kerubistan.kerub.model.Asset
import com.github.kerubistan.kerub.model.AssetOwner
import com.github.kerubistan.kerub.model.AssetOwnerType
import com.github.kerubistan.kerub.model.paging.SearchResultPage
import com.github.kerubistan.kerub.model.paging.SortResultPage
import com.github.kerubistan.kerub.security.AssetAccessController
import com.github.kerubistan.kerub.services.AssetService
import java.util.UUID

abstract class AbstractAssetService<T : Asset>(
		val accessController: AssetAccessController,
		override val dao: AssetDao<T>,
		entityType: String
) : ListableBaseService<T>(entityType), AssetService<T> {

	override fun listByOwner(
			start: Long, limit: Int, sort: String, ownerType: AssetOwnerType, ownerId: UUID
	): SortResultPage<T> {
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

	override fun search(
			field: String, value: String, start: Long, limit: Int, ownerType: AssetOwnerType, ownerId: UUID
	): SearchResultPage<T> {
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
			assertExist(entityType, accessController.doAndCheck {
				super.getById(id)
			}, id)

	override fun update(id: UUID, entity: T): T =
			assertExist(entityType, accessController.checkAndDo(entity) {
				super.update(id, entity)
			}, id)

	final override fun delete(id: UUID) {
		val entity: T = assertExist(entityType, accessController.doAndCheck { dao[id] }, id)

		beforeRemove(entity)

		doRemove(entity)

		afterRemove(entity)
	}

	open fun doRemove(entity: T) {
		dao.remove(entity)
	}

	open fun afterRemove(entity: T) {
		// usually nothing to do after remove, but some virtual resources may need post-action
	}

	open fun beforeRemove(entity: T) {
		//it would be nice to have a generic validation here, like incoming references
	}

	override fun add(entity: T): T =
			accessController.checkAndDo(asset = entity) {
				super.add(entity)
			} ?: entity

	override fun listAll(start: Long, limit: Int, sort: String): SortResultPage<T> =
			accessController.listWithFilter(dao, start, limit, sort)

	override fun getByName(name: String): List<T> = accessController.filter(dao.getByName(name) as List<T>)

	override fun search(field: String, value: String, start: Long, limit: Int): SearchResultPage<T> =
			accessController.searchWithFilter(dao, field, value, start, limit)

	override fun getByNameAndOwner(ownerType: AssetOwnerType, ownerId: UUID, name: String): List<T> =
			TODO("https://github.com/kerubistan/kerub/issues/173")

	override fun autoName(): String {
		//TODO: this is checking globally, it should only be allowed when accounts are not mandatory
		var nr = dao.count() + 1
		var name = "$entityType-$nr"
		while (dao.existsByName(name)) {
			nr++
			name = "$entityType-$nr"
		}
		return name
	}

	override fun autoName(ownerType: AssetOwnerType, ownerId: UUID): String {
		TODO()
	}
}