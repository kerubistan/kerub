package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.controller.Assignment
import java.util.UUID

public trait AssignmentDao : ListableCrudDao<Assignment, UUID> {
	fun listByController(controller: String) : List<Assignment>
}