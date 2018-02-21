package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.data.ControllerDao
import org.infinispan.manager.EmbeddedCacheManager

class ControllerDaoImpl(val cacheManager: EmbeddedCacheManager) : ControllerDao {

	override fun get(id: String): String? = list().firstOrNull { it == id }

	override fun list(): List<String> = cacheManager.members?.map { it.toString() } ?: listOf()
}