package com.github.K0zka.kerub.services.impl

import org.infinispan.manager.DefaultCacheManager
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StatisticsServiceImplTest {

	protected var cacheManager: DefaultCacheManager? = null

	@Before
	fun setUp() {
		cacheManager = DefaultCacheManager()
		cacheManager!!.start()
		cacheManager!!.startCache("TEST")
	}

	@After
	fun cleanup() {
		cacheManager!!.stop()
	}


	@Test
	fun listCaches() {
		val caches = StatisticsServiceImpl(cacheManager!!).listCaches()
		assertTrue(caches.contains("TEST"))
	}

	@Test
	fun getStatisticsInfo() {
		val info = StatisticsServiceImpl(cacheManager!!).getStatisticsInfo("TEST")

	}
}