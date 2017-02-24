package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.ProjectMembership
import java.util.UUID

interface ProjectmembershipDao : CrudDao<ProjectMembership, UUID> {
	fun listByUsername(userName : String) : List<ProjectMembership>
}