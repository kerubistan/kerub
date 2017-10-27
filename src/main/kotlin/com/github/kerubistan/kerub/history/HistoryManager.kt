package com.github.kerubistan.kerub.history

import com.github.kerubistan.kerub.data.AssignmentDao
import com.github.kerubistan.kerub.data.ispn.history.GenericHistoryDaoImpl
import com.github.kerubistan.kerub.host.ControllerManager
import com.github.kerubistan.kerub.model.controller.AssignmentType
import com.github.kerubistan.kerub.utils.getLogger
import java.util.Timer
import java.util.TimerTask

class HistoryManager(private val timer: Timer,
					 private val assignmentDao: AssignmentDao,
					 private val daos: Map<AssignmentType, GenericHistoryDaoImpl<*>>,
					 private val controllerManager: ControllerManager) {

	companion object {
		private val logger = getLogger(HistoryManager::class)
	}

	class Compress(
			private val daos: Map<AssignmentType, GenericHistoryDaoImpl<*>>,
			private val assignmentDao: AssignmentDao,
			private val controllerManager: ControllerManager
	) : TimerTask() {

		override fun run() {

			val controllerId = controllerManager.getControllerId()
			val assignments = assignmentDao.listByController(controllerId).groupBy { it.type }

			daos.forEach { dao ->
				assignments[dao.key]?.map { it.entityId }?.let {
					logger.info("compressing the ${it.size} ${dao.key} entities assigned to this ($controllerId) controller")
					dao.value.compress(0, System.currentTimeMillis(), it)
					logger.info("done")
				}
			}
		}
	}

	fun init() {
		timer.schedule(Compress(daos, assignmentDao, controllerManager), 1L)
	}
}