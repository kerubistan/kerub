package com.github.kerubistan.kerub.planner.steps.network.ovs.port.gre

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.OvsGrePort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.ovs.Vsctl
import io.github.kerubistan.kroki.collections.updateInstances

class CreateOvsGrePortExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val hostConfigurationDao: HostConfigurationDao) : AbstractStepExecutor<CreateOvsGrePort, Unit>() {
	override fun perform(step: CreateOvsGrePort) {
		hostCommandExecutor.execute(step.firstHost) { session ->
			Vsctl.createGrePort(
					session = session,
					bridgeName = step.virtualNetwork.idStr,
					portName = step.name,
					remoteIp = step.secondHost.address
			)
		}
	}

	override fun update(step: CreateOvsGrePort, updates: Unit) {
		hostConfigurationDao.update(step.firstHost.id) { hostConfiguration ->
			hostConfiguration.copy(
					networkConfiguration = hostConfiguration.networkConfiguration.updateInstances(
							selector = { ovsConfig: OvsNetworkConfiguration ->
								ovsConfig.virtualNetworkId == step.virtualNetwork.id
							},
							map = { ovsConfig ->
								ovsConfig.copy(
										ports = ovsConfig.ports +
												OvsGrePort(name = step.name, remoteAddress = step.secondHost.address)
								)
							}
					)
			)
		}
	}
}