package com.github.K0zka.kerub.planner.steps.host.startup

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.planner.StepExecutor
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor
import com.github.K0zka.kerub.utils.getLogger

class WakeHostExecutor(private val hostManager : HostManager, private val hostDynDao : HostDynamicDao) : AbstractStepExecutor<WakeHost>() {

	companion object {
		val logger = getLogger(WakeHostExecutor::class)
		val maxHowWakeRetries = 8
	}

	override fun perform(step: WakeHost) {
		for(nr in 0..maxHowWakeRetries) {
			try {
				logger.debug("attempt {} - waking host {} {}", nr, step.host.address, step.host.id)
				hostManager.getPowerManager(step.host).on()
				logger.debug("attempt {} - connecting host {} {}", nr, step.host.address, step.host.id)
				hostManager.connectHost(step.host)
				logger.debug("attempt {} - host {} {} connected", nr, step.host.address, step.host.id)
				Thread.sleep(30000)
				return
			} catch (e : Exception) {
				logger.debug("attempt {} - connecting {} {}: failed", nr, step.host.address, step.host.id)
			}
		}
		throw Exception("Could not connect host ${step.host.address} ${step.host.id} in $maxHowWakeRetries attempts")
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