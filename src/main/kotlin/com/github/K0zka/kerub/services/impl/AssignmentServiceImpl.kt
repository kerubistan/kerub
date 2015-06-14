package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.AssignmentDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.services.AssignmentService
import com.github.K0zka.kerub.model.controller.Assignment
import com.github.K0zka.kerub.services.HostService
import com.github.K0zka.kerub.utils.getLogger

public class AssignmentServiceImpl(val dao : AssignmentDao, val hostManager : HostManager) : AssignmentService {

	companion object {
		val logger = getLogger(AssignmentServiceImpl::class)
	}

	fun start() {
		logger.info("Starting assignment service")
	}

	override fun listByController(controller: String): List<Assignment> {
		return dao.listByController(controller)
	}

}