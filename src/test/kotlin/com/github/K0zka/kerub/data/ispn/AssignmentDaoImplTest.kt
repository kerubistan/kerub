package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.model.controller.Assignment
import com.github.K0zka.kerub.model.controller.AssignmentType
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class AssignmentDaoImplTest : AbstractIspnDaoTest<UUID, Assignment>() {

	@Test
	fun listByController() {
		val dao = AssignmentDaoImpl(cache!!, eventListener, auditManager)
		val assignment = Assignment(id = UUID.randomUUID(),
				entityId = UUID.randomUUID(),
				controller = "TEST",
				type = AssignmentType.host
		)
		dao.add(assignment)
		val list = dao.listByController("TEST")

		assertEquals(1, list.size)
		assertEquals(assignment, list[0])
	}

	@Test
	fun listByControllerAndType() {
		val dao = AssignmentDaoImpl(cache!!, eventListener, auditManager)
		val assignment = Assignment(id = UUID.randomUUID(),
				entityId = UUID.randomUUID(),
				controller = "TEST",
				type = AssignmentType.host
		)
		dao.add(assignment)
		dao.add(Assignment(id = UUID.randomUUID(),
				entityId = UUID.randomUUID(),
				controller = "SOMETHING OTHER THAN TEST",
				type = AssignmentType.host
		))
		dao.add(Assignment(id = UUID.randomUUID(),
				entityId = UUID.randomUUID(),
				controller = "SOMETHING OTHER THAN TEST",
				type = AssignmentType.vm
		))

		val list = dao.listByControllerAndType("TEST", AssignmentType.host)

		assertEquals(1, list.size)
		assertEquals(assignment, list[0])

	}

}