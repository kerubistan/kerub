package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.AssignmentDao
import com.github.K0zka.kerub.services.AssignmentService
import com.github.K0zka.kerub.model.controller.Assignment

public class AssignmentServiceImpl(val dao : AssignmentDao) : AssignmentService {
	override fun listByController(controller: String): List<Assignment> {
		return dao.listByController(controller)
	}

}