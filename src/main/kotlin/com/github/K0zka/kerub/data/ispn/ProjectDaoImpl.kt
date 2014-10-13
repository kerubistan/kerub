package com.github.K0zka.kerub.data.ispn

import java.util.UUID
import com.github.K0zka.kerub.model.Project
import org.infinispan.Cache
import com.github.K0zka.kerub.data.ProjectDao
import com.github.K0zka.kerub.data.EventListener

public class ProjectDaoImpl(cache : Cache<UUID, Project>, eventListener : EventListener) : ListableIspnDaoBase<Project, UUID>(cache, eventListener), ProjectDao {
	override fun getEntityClass(): Class<Project> {
		return javaClass<Project>()
	}
}