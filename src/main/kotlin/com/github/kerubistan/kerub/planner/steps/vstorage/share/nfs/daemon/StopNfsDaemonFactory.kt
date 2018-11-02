package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.daemon

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.hosts.UnusedService
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import kotlin.reflect.KClass

object StopNfsDaemonFactory : AbstractOperationalStepFactory<StopNfsDaemon>() {
	override val problemHints = setOf(UnusedService::class)
	override val expectationHints = setOf<KClass<out Expectation>>()
	override fun produce(state: OperationalState): List<StopNfsDaemon> =
			state.hosts.values.filter {
				it.config?.services?.let {
					it.contains(NfsDaemonService()) && it.none { it is NfsService }
				} ?: false
			}.map { StopNfsDaemon(it.stat) }
}