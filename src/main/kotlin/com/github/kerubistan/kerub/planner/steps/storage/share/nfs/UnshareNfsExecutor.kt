package com.github.kerubistan.kerub.planner.steps.storage.share.nfs

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.utils.junix.nfs.Exports
import org.apache.sshd.client.session.ClientSession

class UnshareNfsExecutor(override val hostConfigDao: HostConfigurationDao,
						 override val hostExecutor: HostCommandExecutor) :
		AbstractNfsShareExecutor<UnshareNfs>() {

	override fun performOnHost(session: ClientSession, step: UnshareNfs) = Exports.unexport(session, step.directory)

	override fun updateHostConfig(configuration: HostConfiguration, step: UnshareNfs): HostConfiguration =
			configuration.copy(
					services = configuration.services.filterNot { it is NfsService && it.directory == step.directory }
			)


}