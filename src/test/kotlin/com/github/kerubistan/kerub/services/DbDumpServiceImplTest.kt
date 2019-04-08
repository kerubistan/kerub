package com.github.kerubistan.kerub.services

import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.infinispan.AdvancedCache
import org.infinispan.Cache
import org.infinispan.CacheSet
import org.infinispan.commons.util.CloseableIterator
import org.infinispan.manager.EmbeddedCacheManager
import org.junit.Test
import java.io.ByteArrayOutputStream
import javax.ws.rs.core.StreamingOutput

class DbDumpServiceImplTest {

	@Test
	fun dump() {
		val cacheManager = mock<EmbeddedCacheManager>()
		whenever(cacheManager.cacheNames).thenReturn(
				mutableSetOf("testCache")
		)
		val cache = mock<Cache<Any, Any>>()
		val advancedCache = mock<AdvancedCache<Any, Any>>()
		whenever(cacheManager.getCache<Any, Any>(eq("testCache"))).thenReturn(cache)
		whenever(cache.advancedCache).thenReturn(advancedCache)
		val keys = mock<CacheSet<Any>>()
		whenever(keys.iterator()).then { val keyIterator = mock<CloseableIterator<Any>>()
			whenever(keyIterator.hasNext()).thenReturn(true, true, true, false)
			whenever(keyIterator.next()).thenReturn("A", "B", "C")
			keyIterator

		}
		whenever(advancedCache[eq("A")]).thenReturn("A-VALUE")
		whenever(advancedCache[eq("B")]).thenReturn("B-VALUE")
		whenever(advancedCache[eq("C")]).thenReturn("C-VALUE")
		whenever(advancedCache.keys).thenReturn(keys)
		val tar = ByteArrayOutputStream()
		(DbDumpServiceImpl(cacheManager).dump().entity as StreamingOutput).write(tar)

	}
}