package com.github.kerubistan.kerub.data.hub

import com.github.kerubistan.kerub.data.DaoOperations
import com.github.kerubistan.kerub.model.Asset
import com.github.kerubistan.kerub.model.Entity
import java.util.UUID
import kotlin.reflect.KClass

class AnyEntityDaoImpl(
		private val anyAssetDao: AnyAssetDao,
		map: Map<String, DaoOperations.Read<Entity<UUID>, UUID>>
) : AnyEntityDao {

	private val daos = map.map { Class.forName(it.key).kotlin to it.value }.toMap()

	override fun get(clazz: KClass<out Entity<*>>, id: UUID): Entity<UUID>? =
			if (Asset::class.java.isAssignableFrom(clazz.java)) {
				anyAssetDao.get(clazz as KClass<out Asset>, id)
			} else {
				getDao(clazz)[id]
			}

	override fun getAll(clazz: KClass<out Entity<*>>, ids: Collection<UUID>): List<Entity<*>> =
			if (Asset::class.java.isAssignableFrom(clazz.java)) {
				anyAssetDao.getAll(clazz as KClass<out Asset>, ids)
			} else {
				getDao(clazz)[ids]
			}

	private fun getDao(clazz: KClass<out Entity<*>>) = requireNotNull(daos[clazz]) { "dao not set for $clazz" }

}