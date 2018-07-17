package com.github.kerubistan.kerub.planner.steps.host.security.install

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.times

object InstallPublicKeyFactory : AbstractOperationalStepFactory<InstallPublicKey>() {
	override fun produce(state: OperationalState): List<InstallPublicKey> =
			state.runningHosts.let { runningHosts ->

				(runningHosts * runningHosts).filter { (source, target) ->
					source.stat.id != target.stat.id
							&& source.config?.publicKey != null
							&& (target.config?.acceptedPublicKeys?.let { !it.contains(source.config.publicKey) }
							?: false)
				}.map { (source, target) ->
					InstallPublicKey(sourceHost = source.stat, targetHost = target.stat, publicKey = source.config!!.publicKey!!)
				}
			}
}