package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.common

import com.github.k0zka.finder4j.backtrack.Step
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.SimilarStep
import com.github.kerubistan.kerub.planner.steps.storage.lvm.base.updateHostDynLvmWithAllocation
import io.github.kerubistan.kroki.collections.update
import java.math.BigInteger

abstract class AbstractPoolResizeStep : AbstractOperationalStep, SimilarStep {
	abstract val host: Host
	abstract val vgName: String
	abstract val pool: String

	final override fun isLikeStep(other: Step<Plan>): Boolean = other.let {
		it is AbstractPoolResizeStep && it.host == host && it.vgName == vgName && it.pool == pool
	}

	override fun take(state: OperationalState): OperationalState = state.copy(
			hosts = state.hosts.update(host.id) { hostData ->
				hostData.copy(
						config = requireNotNull(hostData.config).let { hostConfig ->
							hostConfig.copy(
									storageConfiguration = hostConfig.storageConfiguration.map {
										if (it is LvmPoolConfiguration && it.poolName == pool) {
											it.copy(size = it.size + sizeChange())
										} else it
									}
							)
						},
						dynamic = updateHostDynLvmWithAllocation(state, host, vgName, sizeChange())
				)
			}
	)

	internal abstract fun sizeChange(): BigInteger

}