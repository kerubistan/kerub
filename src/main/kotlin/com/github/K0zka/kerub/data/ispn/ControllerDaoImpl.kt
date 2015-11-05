package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.ControllerDao
import org.infinispan.manager.EmbeddedCacheManager

public class ControllerDaoImpl(val cacheManager: EmbeddedCacheManager) : ControllerDao {

	override fun get(id: String): String? {
		return list().filter { it == id }.firstOrNull()
	}

	override fun list(): List<String> {
		return cacheManager.getMembers()?.map { it.toString() } ?: listOf()
	}
}