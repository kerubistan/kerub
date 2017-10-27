package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.ProjectDao
import com.github.kerubistan.kerub.model.Project
import com.github.kerubistan.kerub.services.ProjectService

class ProjectServiceImpl(override val dao: ProjectDao) : ListableBaseService<Project>("project"), ProjectService