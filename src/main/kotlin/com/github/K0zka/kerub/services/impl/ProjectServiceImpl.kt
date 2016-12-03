package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.ProjectDao
import com.github.K0zka.kerub.model.Project
import com.github.K0zka.kerub.services.ProjectService

class ProjectServiceImpl(override val dao: ProjectDao) : ListableBaseService<Project>("project"), ProjectService {
}