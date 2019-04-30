package com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.factoryFeature
import com.github.kerubistan.kerub.utils.any
import com.github.kerubistan.kerub.utils.junix.nfs.Exports
import kotlin.reflect.KClass

object StartNfsDaemonFactory : AbstractOperationalStepFactory<StartNfsDaemon>() {
	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<StartNfsDaemon> =
			factoryFeature(state.controllerConfig.storageTechnologies.nfsEnabled) {
				state.index.runningHosts
						.filter { Exports.available(it.stat.capabilities) }
						.filterNot { it.config?.services?.any<NfsDaemonService>() ?: false }
						.map { StartNfsDaemon(it.stat) }
			}
}