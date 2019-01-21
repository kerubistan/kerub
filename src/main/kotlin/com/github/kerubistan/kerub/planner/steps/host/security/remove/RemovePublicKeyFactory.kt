package com.github.kerubistan.kerub.planner.steps.host.security.remove

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.join
import kotlin.reflect.KClass

object RemovePublicKeyFactory : AbstractOperationalStepFactory<RemovePublicKey>() {
	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<RemovePublicKey> =
			state.connectionTargets.map { (hostId, servers) ->
				val host = requireNotNull(state.hosts[hostId])
				servers.map { server ->
					RemovePublicKey(host = server, hostOfKey = host.stat, publicKey = host.config?.publicKey!!)
				}
			}.join()

}