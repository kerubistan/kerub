package com.github.K0zka.kerub.data.ispn

import java.util.UUID
import com.github.K0zka.kerub.model.Project
import org.infinispan.Cache
import com.github.K0zka.kerub.data.ProjectDao

public class ProjectDaoImpl(cache : Cache<UUID, Project>) : ListableIspnDaoBase<Project, UUID>(cache), ProjectDao {
	override fun getEntityClass(): Class<Project> {
		return javaClass<Project>()
	}
}