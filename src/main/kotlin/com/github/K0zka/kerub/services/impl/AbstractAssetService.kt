package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.ListableCrudDao
import com.github.K0zka.kerub.model.Asset
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.paging.SortResultPage
import com.github.K0zka.kerub.security.AccessController
import com.github.K0zka.kerub.security.admin
import org.apache.shiro.SecurityUtils.getSubject
import java.util.UUID

abstract class AbstractAssetService<T : Asset>(
		val accessController: AccessController,
		dao: ListableCrudDao<T, UUID>,
		entityType: String
) : ListableBaseService<T>(dao, entityType) {

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

	override fun add(entity: T): T {
		return accessController.checkAndDo(asset = entity) {
			super.add(entity)
		} ?: entity
	}

	override fun listAll(start: Long, limit: Long, sort: String): SortResultPage<T> {
		if (getSubject().hasRole(admin)) {

		}
		return super.listAll(start, limit, sort)
	}
}