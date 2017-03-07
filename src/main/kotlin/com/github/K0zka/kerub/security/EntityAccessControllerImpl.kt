package com.github.K0zka.kerub.security

import com.github.K0zka.kerub.data.hub.AnyAssetDao
import com.github.K0zka.kerub.model.Asset
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.model.annotations.Dynamic
import com.github.K0zka.kerub.model.dynamic.DynamicEntity
import org.apache.shiro.SecurityUtils
import kotlin.reflect.KClass

class EntityAccessControllerImpl(
		private val assetAccessController: AssetAccessController,
		private val anyAssetDao: AnyAssetDao
) : EntityAccessController {

	companion object {
		internal fun statClassOf(clazz: KClass<out Entity<*>>): KClass<out Entity<*>>? =
				clazz.java.getAnnotation(Dynamic::class.java)?.value
	}

	internal fun statFromDynamic(obj: DynamicEntity): Any?
			= statClassOf(obj.javaClass.kotlin)?.let {
		clazz ->
		anyAssetDao.get(clazz as KClass<out Asset>, obj.id)
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
					} else null
				}
			else ->
				// any other case: only if the user is admin and has access to everything (hosts, network)
				// and not only virtual assets
				if (SecurityUtils.getSubject().hasRole(admin)) {
					action()
				} else null
		}

	}
}