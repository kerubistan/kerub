package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.audit.AuditManager
import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.data.ProjectMembershipDao
import com.github.kerubistan.kerub.model.ProjectMembership
import org.infinispan.Cache
import java.util.UUID

class ProjectMembershipDaoImpl(
		cache: Cache<UUID, ProjectMembership>,
		eventListener: EventListener,
		auditManager: AuditManager
) : ProjectMembershipDao, ListableIspnDaoBase<ProjectMembership, UUID>(cache, eventListener, auditManager) {

	override fun listByUsername(userName: String): List<ProjectMembership> =
			cache.queryBuilder(ProjectMembership::class)
					.having(ProjectMembership::user.name).eq(userName)
					.list()

	override fun getEntityClass(): Class<ProjectMembership> =
			ProjectMembership::class.java
}