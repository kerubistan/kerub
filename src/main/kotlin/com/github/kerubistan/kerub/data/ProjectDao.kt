package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.Project
import java.util.UUID

interface ProjectDao : ListableCrudDao<Project, UUID>