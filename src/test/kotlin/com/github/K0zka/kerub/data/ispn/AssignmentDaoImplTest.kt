package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.AssignmentDao
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.model.controller.Assignment
import com.github.K0zka.kerub.model.controller.AssignmentType
import com.nhaarman.mockito_kotlin.mock
import org.infinispan.Cache
import org.infinispan.manager.DefaultCacheManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import java.util.UUID
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class) class AssignmentDaoImplTest {
	var cacheManager : DefaultCacheManager? = null
	var cache : Cache<UUID, Assignment>? = null
	var dao  : AssignmentDao? = null
	@Mock var eventListener : EventListener? = null

	val auditManager = mock<AuditManager>()

	@Before fun setup() {
		cacheManager = DefaultCacheManager()
		cacheManager!!.start()
		cache = cacheManager!!.getCache("test")
		cache!!.clear()
		dao = AssignmentDaoImpl(cache!!, eventListener!!, auditManager)
	}

	@After fun cleanup() {
		cacheManager?.stop()
	}

	@Test
	fun listByController() {
		val assignment = Assignment(id = UUID.randomUUID(),
		                            entityId = UUID.randomUUID(),
		                            controller = "TEST",
									type = AssignmentType.host
		                            )
		dao!!.add(assignment)
		val list = dao!!.listByController("TEST")

		assertEquals(1, list.size)
		assertEquals(assignment, list[0])
	}

	@Test
	fun listByControllerAndType() {
		val assignment = Assignment(id = UUID.randomUUID(),
				entityId = UUID.randomUUID(),
				controller = "TEST",
				type = AssignmentType.host
		)
		dao!!.add(assignment)
		dao!!.add(Assignment(id = UUID.randomUUID(),
				entityId = UUID.randomUUID(),
				controller = "SOMETHING OTHER THAN TEST",
				type = AssignmentType.host
		))
		dao!!.add(Assignment(id = UUID.randomUUID(),
				entityId = UUID.randomUUID(),
				controller = "SOMETHING OTHER THAN TEST",
				type = AssignmentType.vm
		))

		val list = dao!!.listByControllerAndType("TEST", AssignmentType.host)

		assertEquals(1, list.size)
		assertEquals(assignment, list[0])

	}

}