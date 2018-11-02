package com.github.kerubistan.kerub.planner.steps.host.security.generate

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import kotlin.reflect.KClass

object GenerateSshKeyFactory : AbstractOperationalStepFactory<GenerateSshKey>() {
	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<GenerateSshKey> = state.hosts.values
			.filter { it.dynamic?.status == HostStatus.Up && it.config?.publicKey == null }
			.map { GenerateSshKey(host = it.stat) }
}