package com.github.kerubistan.kerub.planner.steps.storage.share.nfs

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.utils.junix.nfs.Exports
import org.apache.sshd.client.session.ClientSession

class ShareNfsExecutor(override val hostConfigDao: HostConfigurationDao,
					   override val hostExecutor: HostCommandExecutor) : AbstractNfsShareExecutor<ShareNfs>() {
	override fun performOnHost(session: ClientSession, step: ShareNfs) =
			Exports.export(session, step.directory, step.write)

	override fun updateHostConfig(configuration: HostConfiguration,
								  step: ShareNfs): HostConfiguration {
		return configuration.copy(
				services = configuration.services + NfsService(directory = step.directory, write = step.write)
		)
	}

}