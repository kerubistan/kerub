package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.controller.Assignment
import com.github.K0zka.kerub.model.controller.AssignmentType
import java.util.UUID

interface AssignmentDao : ListableCrudDao<Assignment, UUID> {
	fun listByController(controller: String): List<Assignment>
	fun listByControllerAndType(controller: String, type: AssignmentType): List<Assignment>
}