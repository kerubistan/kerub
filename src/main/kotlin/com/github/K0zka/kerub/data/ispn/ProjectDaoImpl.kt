package com.github.K0zka.kerub.data.ispn

import java.util.UUID
import com.github.K0zka.kerub.model.Project
import org.infinispan.Cache
import com.github.K0zka.kerub.data.ProjectDao

public class ProjectDaoImpl(cache : Cache<UUID, Project>) : IspnDaoBase<Project, UUID>(cache), ProjectDao {
	override fun listAll(): List<Project> {
		return cache.values().toList()
	}
}