package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.controller.Assignment
import com.github.kerubistan.kerub.model.controller.AssignmentType
import java.util.UUID

interface AssignmentDao : ListableCrudDao<Assignment, UUID> {
	fun listByController(controller: String): List<Assignment>
	fun listByControllerAndType(controller: String, type: AssignmentType): List<Assignment>
}