package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.audit.AuditManager
import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.data.ProjectDao
import com.github.kerubistan.kerub.model.Project
import org.infinispan.Cache
import java.util.UUID

class ProjectDaoImpl(cache: Cache<UUID, Project>, eventListener: EventListener, auditManager: AuditManager)
	: ListableIspnDaoBase<Project, UUID>(cache, eventListener, auditManager), ProjectDao {
	override fun getEntityClass(): Class<Project> = Project::class.java
}