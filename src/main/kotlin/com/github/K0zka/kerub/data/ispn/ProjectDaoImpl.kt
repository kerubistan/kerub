package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.ProjectDao
import com.github.K0zka.kerub.model.Project
import org.infinispan.Cache
import java.util.UUID

class ProjectDaoImpl(cache: Cache<UUID, Project>, eventListener: EventListener) : ListableIspnDaoBase<Project, UUID>(cache, eventListener), ProjectDao {
	override fun getEntityClass(): Class<Project> {
		return Project::class.java
	}
}