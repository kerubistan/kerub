package com.github.kerubistan.kerub.planner.steps.host.startup

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.host.lom.WakeOnLan
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.getLogger

open class WakeHostExecutor(
		private val hostManager: HostManager,
		private val hostDynDao: HostDynamicDao,
		private val tries: Int = defaultMaxRetries,
		private val wait: Long = defaultWaitBetweenTries
) : AbstractStepExecutor<AbstractWakeHost, Unit>() {

	companion object {
		private val logger = getLogger()
		private const val defaultMaxRetries = 8
		private const val defaultWaitBetweenTries = 30000.toLong()
	}

	override fun perform(step: AbstractWakeHost) {
		var lastException: Exception? = null
		for (nr in 0..tries) {
			if (hostDynDao[step.host.id]?.status == HostStatus.Up) {
				//host connected
				return
			}
			try {
				logger.debug("attempt {} - waking host {} {}", nr, step.host.address, step.host.id)
				when (step) {
					is WolWakeHost -> {
						wakeOnLoan(step.host)
					}
					else -> TODO()
				}
				logger.debug("attempt {} - connecting host {} {}", nr, step.host.address, step.host.id)
				hostManager.connectHost(step.host)
				logger.debug("attempt {} - host {} {} connected", nr, step.host.address, step.host.id)
				return
			} catch (e: Exception) {
				logger.debug("attempt {} - connecting {} {}: failed - waiting {} ms before retry",
						nr, step.host.address, step.host.id, wait)
				Thread.sleep(wait)
				lastException = e
			}
		}
		throw WakeHostException(
				"Could not connect host ${step.host.address} ${step.host.id} in $defaultMaxRetries attempts",
				lastException)
	}

	internal open fun wakeOnLoan(host: Host) {
		WakeOnLan(host).on()
	}

	override fun update(step: AbstractWakeHost, updates: Unit) {
		hostDynDao.update(step.host.id) { dyn ->
			dyn.copy(
					status = HostStatus.Up
			)
		}
	}

}