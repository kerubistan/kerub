package com.github.kerubistan.kerub.planner.steps.network.ovs.sw.remove

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.ovs.Vsctl

class RemoveOvsSwitchExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val hostConfigurationDao: HostConfigurationDao
) : AbstractStepExecutor<RemoveOvsSwitch, Unit>() {
	override fun perform(step: RemoveOvsSwitch) =
			hostCommandExecutor.execute(step.host) { session ->
				Vsctl.removeBridge(session, bridgeName = step.virtualNetwork.idStr)
			}

	override fun update(step: RemoveOvsSwitch, updates: Unit) =
			hostConfigurationDao.update(step.host.id) { hostConfig ->
				hostConfig.copy(
						networkConfiguration = hostConfig.networkConfiguration
								.filterNot { it.virtualNetworkId == step.virtualNetwork.id }
				)
			}
}