package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.AssetDao
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.model.Asset
import com.github.K0zka.kerub.model.AssetOwner
import org.infinispan.Cache
import org.infinispan.query.Search
import org.infinispan.query.dsl.Query
import java.util.UUID

abstract class AbstractAssetDao<T : Asset>(
		cache: Cache<UUID, T>,
		eventListener: EventListener,
		auditManager: AuditManager
) : AssetDao<T>, ListableIspnDaoBase<T, UUID>(cache, eventListener, auditManager) {

	override fun listByOwners(owners: Collection<AssetOwner>, start: Long, limit: Long, sort: String): List<T> =
			cache.queryBuilder(Asset::class)
					.having(Asset::owner.name).`in`(owners)
					.toBuilder<Query>().build().list<T>()

	override fun listByOwner(owner: AssetOwner, start: Long, limit: Long, sort: String): List<T> =
			cache.queryBuilder(Asset::class)
					.having(Asset::owner.name).eq(owner)
					.toBuilder<Query>().build().list<T>()

}