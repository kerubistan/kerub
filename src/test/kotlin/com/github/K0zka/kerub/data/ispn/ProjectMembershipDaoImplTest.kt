package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.model.ProjectMembership
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class ProjectMembershipDaoImplTest : AbstractIspnDaoTest<UUID, ProjectMembership>() {

	@Test
	fun listByUsername() {
		val dao = ProjectMembershipDaoImpl(cache!!, eventListener, auditManager)
		val projectMembership = ProjectMembership(
				id = UUID.randomUUID(),
				groupId = UUID.randomUUID(),
				user = "TEST",
				quota = null
		)
		dao.add(
				projectMembership
		)
		assertEquals(listOf<ProjectMembership>(), dao.listByUsername("SOMETHING-ELSE"))
		assertEquals(listOf(projectMembership), dao.listByUsername("TEST"))
	}
}