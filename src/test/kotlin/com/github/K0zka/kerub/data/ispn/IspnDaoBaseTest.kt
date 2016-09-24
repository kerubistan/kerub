package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.model.Entity
import com.nhaarman.mockito_kotlin.mock
import org.infinispan.Cache
import org.infinispan.manager.DefaultCacheManager
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

class IspnDaoBaseTest {

	class TestEntity : Entity<String> {
		override var id: String = "TEST"
	}

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
		val entity = dao!!.get("A")
		Assert.assertEquals(orig, entity)
	}
}