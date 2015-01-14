package com.github.K0zka.kerub.data.ispn

import org.infinispan.manager.DefaultCacheManager
import org.infinispan.Cache
import com.github.K0zka.kerub.data.ispn.IspnDaoBaseTest.TestEntity
import org.mockito.Mock
import com.github.K0zka.kerub.data.EventListener
import org.junit.Before
import com.github.K0zka.kerub.data.ispn.IspnDaoBaseTest.TestDao
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import org.junit.Test
import com.github.K0zka.kerub.model.controller.Assignment
import com.github.K0zka.kerub.data.AssignmentDao
import java.util.UUID
import kotlin.test.assertEquals
import org.junit.After

RunWith(javaClass<MockitoJUnitRunner>())
public class AssignmentDaoImplTest {
	var cacheManager : DefaultCacheManager? = null
	var cache : Cache<UUID, Assignment>? = null
	var dao  : AssignmentDao? = null
	Mock var eventListener : EventListener? = null

	Before fun setup() {
		cacheManager = DefaultCacheManager()
		cacheManager!!.start()
		cache = cacheManager!!.getCache("test")
		cache!!.clear()
		dao = AssignmentDaoImpl(cache!!, eventListener!!)
	}

	After fun cleanup() {
		cacheManager?.stop()
	}

	Test
	fun listByController() {
		val assignment = Assignment(id = UUID.randomUUID(),
		                            hostId = UUID.randomUUID(),
		                            controller = "TEST"
		                            )
		dao!!.add(assignment)
		val list = dao!!.listByController("TEST")

		assertEquals(1, list.size)
		assertEquals(assignment, list[0])
	}

}