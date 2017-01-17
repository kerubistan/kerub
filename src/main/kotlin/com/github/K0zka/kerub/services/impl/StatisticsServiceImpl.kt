package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.services.StatisticsInfo
import com.github.K0zka.kerub.services.StatisticsService
import org.infinispan.AdvancedCache
import org.infinispan.manager.EmbeddedCacheManager

class StatisticsServiceImpl(
		private val cacheManager: EmbeddedCacheManager
) : StatisticsService {

	override fun listCaches(): List<String> =
		cacheManager.cacheNames.toList().sorted()

	override fun getStatisticsInfo(cacheName: String): StatisticsInfo {
		val stats = (cacheManager.getCache<Any, Any>(cacheName) as AdvancedCache<Any, Any>).stats
		return StatisticsInfo(
				avgReadTime = stats?.averageReadTime ?: -1,
				avgRemoveTime = stats?.averageRemoveTime ?: -1,
				avgWriteTime = stats?.averageWriteTime ?: -1,
				hits = stats?.hits ?: -1,
				misses = stats?.misses ?: -1,
				nrEntries = stats?.totalNumberOfEntries ?: -1,
				upTime = stats?.timeSinceStart ?: -1)
	}
}