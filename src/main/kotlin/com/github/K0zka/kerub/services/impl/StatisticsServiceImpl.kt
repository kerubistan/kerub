package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.services.StatisticsService
import com.github.K0zka.kerub.services.StatisticsInfo
import org.infinispan.manager.EmbeddedCacheManager
import org.infinispan.AdvancedCache

public class StatisticsServiceImpl(val cacheManager : EmbeddedCacheManager) : StatisticsService {
	override fun getStatisticsInfo(cacheName: String): StatisticsInfo {
		val stats = (cacheManager.getCache<Any, Any>(cacheName) as AdvancedCache<Any, Any>).getStats()
		return StatisticsInfo(
				avgReadTime = stats?.getAverageReadTime() ?: -1,
				avgRemoveTime = stats?.getAverageRemoveTime() ?: -1,
				avgWriteTime = stats?.getAverageWriteTime() ?: -1,
				hits = stats?.getHits() ?: -1,
				misses = stats?.getMisses() ?: -1,
				nrEntries = stats?.getTotalNumberOfEntries() ?: -1,
				upTime = stats?.getTimeSinceStart() ?: -1)
	}
}