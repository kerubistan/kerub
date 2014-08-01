package com.github.K0zka.kerub.services.impl

import java.util.UUID
import com.github.K0zka.kerub.model.Project
import com.github.K0zka.kerub.data.ProjectDao
import com.github.K0zka.kerub.services.ProjectService

public class ProjectServiceImpl (dao : ProjectDao) : BaseServiceImpl<Project>(dao, "project"), ProjectService {
}