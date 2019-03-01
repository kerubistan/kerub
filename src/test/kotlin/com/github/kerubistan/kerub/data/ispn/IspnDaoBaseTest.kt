package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.audit.AuditManager
import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.model.Entity
import com.nhaarman.mockito_kotlin.mock
import org.infinispan.Cache
import org.infinispan.manager.DefaultCacheManager
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class IspnDaoBaseTest {

	data class TestEntity(override var id: String = "TEST") : Entity<String>

	class TestDao(cache: Cache<String, TestEntity>, eventListener: EventListener, auditManager: AuditManager)
	: ListableIspnDaoBase<TestEntity, String>(cache, eventListener, auditManager) {
		override fun getEntityClass(): Class<TestEntity> {
			return TestEntity::class.java
		}


	}

	var cacheManager: DefaultCacheManager? = null
	var cache: Cache<String, TestEntity>? = null
	var dao: IspnDaoBase<TestEntity, String>? = null
	val eventListener: EventListener = mock()
	val auditManager = mock<AuditManager>()


	@Before fun setup() {
		cacheManager = DefaultCacheManager()
		cacheManager!!.start()
		cache = cacheManager!!.getCache("test")
		dao = TestDao(cache!!, eventListener, auditManager)
	}

	@After
	fun cleanup() {
		cacheManager?.stop()
	}

	@Test
	fun get() {
		val orig = TestEntity()
		cache!!.put("A", orig)
		val entity = dao!!["A"]
		Assert.assertEquals(orig, entity)
	}
}