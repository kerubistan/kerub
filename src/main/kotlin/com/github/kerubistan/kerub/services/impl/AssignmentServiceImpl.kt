package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.AssignmentDao
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.model.controller.Assignment
import com.github.kerubistan.kerub.services.AssignmentService
import com.github.kerubistan.kerub.utils.getLogger

class AssignmentServiceImpl(val dao: AssignmentDao, val hostManager: HostManager) : AssignmentService {

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