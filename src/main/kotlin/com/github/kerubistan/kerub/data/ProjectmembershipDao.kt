package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.ProjectMembership
import java.util.UUID

interface ProjectmembershipDao : CrudDao<ProjectMembership, UUID> {
	fun listByUsername(userName: String): List<ProjectMembership>
}