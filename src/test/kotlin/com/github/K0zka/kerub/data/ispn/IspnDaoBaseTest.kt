package com.github.K0zka.kerub.data.ispn

import org.junit.Test
import org.junit.Before
import com.github.K0zka.kerub.model.Entity
import org.infinispan.manager.DefaultCacheManager
import org.infinispan.Cache
import org.junit.Assert

class IspnDaoBaseTest {

	class TestEntity : Entity<String>{
		override var id: String? = null
	}

	var cache : Cache<String, TestEntity>? = null
	var dao  : IspnDaoBase<TestEntity, String>? = null

	Before fun setup() {
		cache = DefaultCacheManager().getCache("test")
		dao = IspnDaoBase<TestEntity, String>(cache!!)
	}

	Test
	fun get() {
		val orig = TestEntity()
		cache!!.put("A", orig)
		val entity = dao!!.get("A")
		Assert.assertEquals(orig, entity)
	}
}