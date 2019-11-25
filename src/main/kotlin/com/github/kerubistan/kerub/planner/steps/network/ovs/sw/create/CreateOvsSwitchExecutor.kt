package com.github.kerubistan.kerub.planner.steps.network.ovs.sw.create

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.ovs.Vsctl

class CreateOvsSwitchExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val hostConfigurationDao: HostConfigurationDao
) : AbstractStepExecutor<CreateOvsSwitch, Unit>() {
	override fun perform(step: CreateOvsSwitch) {
		hostCommandExecutor.execute(step.host) { session ->
			Vsctl.createBridge(session, step.network.idStr)
		}
	}

	override fun update(step: CreateOvsSwitch, updates: Unit) {
		hostConfigurationDao.updateWithDefault(step.host.id,
				defaultValue = { HostConfiguration(id = step.host.id) }) { hostConfig ->
			hostConfig.copy(
					networkConfiguration = hostConfig.networkConfiguration
							+ OvsNetworkConfiguration(virtualNetworkId = step.network.id)
			)
		}
	}
}