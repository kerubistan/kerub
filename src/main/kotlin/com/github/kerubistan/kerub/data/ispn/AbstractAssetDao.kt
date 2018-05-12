package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.audit.AuditManager
import com.github.kerubistan.kerub.data.AssetDao
import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.model.Asset
import com.github.kerubistan.kerub.model.AssetOwner
import com.github.kerubistan.kerub.model.Named
import org.infinispan.Cache
import org.infinispan.query.dsl.Expression
import java.util.UUID

abstract class AbstractAssetDao<T : Asset>(
		cache: Cache<UUID, T>,
		eventListener: EventListener,
		auditManager: AuditManager
) : AssetDao<T>, ListableIspnDaoBase<T, UUID>(cache, eventListener, auditManager) {

	override fun fieldSearch(field: String, value: String, start: Long, limit: Int): List<T> =
			cache.fieldSearch(getEntityClass().kotlin, field, value, start, limit)

	override fun fieldSearch(owners: Set<AssetOwner>, field: String, value: String, start: Long, limit: Int): List<T> =
			if (owners.isEmpty()) {
				listOf()
			} else {
				cache.queryBuilder(getEntityClass().kotlin)
						.startOffset(start)
						.maxResults(limit)
						.having(field).like("%$value%")
						.and()
						.having(Asset::ownerIdStr.name).`in`(owners.map { it.ownerIdStr })
						.list()
			}

	override fun listByOwners(owners: Collection<AssetOwner>, start: Long, limit: Int, sort: String): List<T> =
			if (owners.isEmpty()) {
				listOf()
			} else {
				cache.queryBuilder(getEntityClass().kotlin)
						.startOffset(start)
						.maxResults(limit)
						.having(Asset::ownerIdStr.name).`in`(owners.map { it.ownerIdStr })
						.list()
			}

	override fun listByOwner(owner: AssetOwner, start: Long, limit: Int, sort: String): List<T> =
			cache.queryBuilder(getEntityClass().kotlin)
					.startOffset(start)
					.maxResults(limit)
					.having(Asset::ownerIdStr.name).eq(owner.ownerIdStr)
					.list()

	override fun count(owners: Set<AssetOwner>): Int =
			if (owners.isEmpty()) {
				0
			} else {
				val count = cache.queryBuilder(getEntityClass().kotlin)
						.select(Expression.count(Asset::name.name))
						.having(Asset::ownerIdStr.name).`in`(owners.map { it.ownerIdStr })
						.list<Asset>().first() as Array<*>
				(count.first() as Number).toInt()
			}

	override fun getByName(name: String, max: Int?): List<T> =
			cache.queryBuilder(getEntityClass().kotlin)
					.let {
						if (max != null) {
							it.maxResults(max)
						}
						it
					}
					.having(Named::name.name).eq(name)
					.list()

	override fun getByName(owner: AssetOwner, name: String) =
			cache.queryBuilder(getEntityClass().kotlin)
					.having(Named::name.name).eq(name)
					.and().having(Asset::ownerIdStr.name).eq(owner.ownerIdStr)
					.list<T>()

}