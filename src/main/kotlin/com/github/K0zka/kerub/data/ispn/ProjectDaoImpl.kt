package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.ProjectDao
import com.github.K0zka.kerub.model.Project
import org.infinispan.Cache
import java.util.UUID

class ProjectDaoImpl(cache: Cache<UUID, Project>, eventListener: EventListener, auditManager: AuditManager)
: ListableIspnDaoBase<Project, UUID>(cache, eventListener, auditManager), ProjectDao {
	override fun getEntityClass(): Class<Project> {
		return Project::class.java
	}
}