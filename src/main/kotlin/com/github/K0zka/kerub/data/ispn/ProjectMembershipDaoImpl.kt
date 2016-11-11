package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.ProjectmembershipDao
import com.github.K0zka.kerub.model.ProjectMembership
import org.infinispan.Cache
import java.util.UUID

class ProjectMembershipDaoImpl(cache: Cache<UUID, ProjectMembership>,
							   eventListener: EventListener,
							   auditManager: AuditManager) : ProjectmembershipDao, ListableIspnDaoBase<ProjectMembership, UUID>(cache, eventListener, auditManager) {
	override fun getEntityClass(): Class<ProjectMembership> =
			ProjectMembership::class.java
}