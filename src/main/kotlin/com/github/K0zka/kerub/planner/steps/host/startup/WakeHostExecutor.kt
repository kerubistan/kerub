package com.github.K0zka.kerub.planner.steps.host.startup

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor
import com.github.K0zka.kerub.utils.getLogger

class WakeHostExecutor(
		private val hostManager: HostManager,
		private val hostDynDao: HostDynamicDao,
		private val tries : Int = defaulMaxRetries,
		private val wait : Long = defaultWaitBetweenTries
) : AbstractStepExecutor<WakeHost>() {

	companion object {
		val logger = getLogger(WakeHostExecutor::class)
		val defaulMaxRetries = 8
		val defaultWaitBetweenTries = 30000.toLong()
	}

	override fun perform(step: WakeHost) {
		for (nr in 0..tries) {
			if(hostDynDao.get(step.host.id)?.status == HostStatus.Up) {
				//host connected
				return
			}
			try {
				logger.debug("attempt {} - waking host {} {}", nr, step.host.address, step.host.id)
				hostManager.getPowerManager(step.host).on()
				logger.debug("attempt {} - connecting host {} {}", nr, step.host.address, step.host.id)
				hostManager.connectHost(step.host)
				logger.debug("attempt {} - host {} {} connected", nr, step.host.address, step.host.id)
				return
			} catch (e: Exception) {
				logger.debug("attempt {} - connecting {} {}: failed - waiting {} ms before retry",
						nr, step.host.address, step.host.id, wait)
				Thread.sleep(wait)
			}
		}
		throw Exception("Could not connect host ${step.host.address} ${step.host.id} in $defaulMaxRetries attempts")
	}

	override fun update(step: WakeHost) {
		hostDynDao.update(step.host.id, {
			dyn ->
			dyn.copy(
					status = HostStatus.Up
			)
		})
	}

}