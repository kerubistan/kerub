package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.audit.AuditManager
import com.github.kerubistan.kerub.data.EventListener
import com.nhaarman.mockito_kotlin.mock
import org.infinispan.Cache
import org.infinispan.configuration.cache.ConfigurationBuilder
import org.infinispan.manager.DefaultCacheManager
import org.junit.After
import org.junit.Before

abstract class AbstractIspnDaoTest<K: Any, V : Any> {

	private var cacheManager : DefaultCacheManager? = null
	protected var cache : Cache<K, V>? = null
	protected val auditManager = mock<AuditManager>()
	protected val eventListener: EventListener = mock()

	@Before
	fun setUp() {
		val configuration = ConfigurationBuilder().invocationBatching().enable().build()
		cacheManager = DefaultCacheManager(configuration)
		cacheManager!!.start()
		cache = cacheManager!!.getCache("test")
		cache!!.clear()
	}

	@After
	fun cleanup() {
		cache!!.clear()
		cacheManager!!.stop()
	}

}