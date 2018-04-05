package com.github.kerubistan.kerub.planner.steps.vstorage.mount

import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.factoryFeature
import com.github.kerubistan.kerub.utils.join

object MountNfsFactory : AbstractOperationalStepFactory<MountNfs>() {

	override fun produce(state: OperationalState): List<MountNfs> =
			factoryFeature(state.controllerConfig.storageTechnologies.nfsEnabled) {
				// for each host, all other host's all existing shares, except the ones that are already mounted
				state.hosts.values.map { hostColl ->
					(state.hosts - hostColl.stat.id).map { otherHost ->
						otherHost.value.stat to
								otherHost.value.config?.services?.filterIsInstance<NfsService>()?.filter { nfsService ->
									hostColl.config?.services?.none {
										it is NfsMount
												&& it.remoteHostId == otherHost.key
												&& it.remoteDirectory == nfsService.directory
									}
											?: true
								}
					}.map {
						remoteAndShares ->
						val remote = remoteAndShares.first
						val shares = remoteAndShares.second
						shares?.map { MountNfs(host = hostColl.stat,
											   remoteHost = remote,
											   directory = "/mnt/${remote.id}/${it.directory}",
											   remoteDirectory = it.directory)
						}
					}.filterNotNull().join()
				}.join()
			}
}