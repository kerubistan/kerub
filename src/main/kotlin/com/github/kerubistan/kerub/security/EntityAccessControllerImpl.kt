package com.github.kerubistan.kerub.security

import com.github.kerubistan.kerub.data.hub.AnyEntityDao
import com.github.kerubistan.kerub.model.Asset
import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.annotations.Dynamic
import com.github.kerubistan.kerub.model.dynamic.DynamicEntity
import org.apache.shiro.SecurityUtils
import java.util.UUID
import kotlin.reflect.KClass

class EntityAccessControllerImpl(
		private val assetAccessController: AssetAccessController,
		private val anyEntityDao: AnyEntityDao
) : EntityAccessController {

	companion object {
		internal fun statClassOf(clazz: KClass<out Entity<*>>): KClass<out Entity<*>>? =
				clazz.java.getAnnotation(Dynamic::class.java)?.value
	}

	internal fun statFromDynamic(obj: DynamicEntity): Entity<UUID>?
			= statClassOf(obj.javaClass.kotlin)?.let {
		clazz ->
		anyEntityDao.get(clazz, obj.id)
	}

	override fun <T> checkAndDo(obj: Entity<*>, action: () -> T?): T? {
		return when (obj) {
			is Asset ->
				//if asset, the asset controller decides
				assetAccessController.checkAndDo(obj, action)
			is DynamicEntity ->
				//if it is a dyn record of an asset, let's get the asset and decide based on that
				statFromDynamic(obj)?.let {
					stat ->
					if (stat is Asset) {
						assetAccessController.checkAndDo(stat, action)
					} else handleNonAssetChanges(action)
				}
			else ->
				// any other case: only if the user is admin and has access to everything (hosts, network)
				// and not only virtual assets
				handleNonAssetChanges(action)
		}

	}

	private fun <T> handleNonAssetChanges(action: () -> T?): T? {
		return if (SecurityUtils.getSubject().hasRole(admin)) {
			action()
		} else null
	}
}