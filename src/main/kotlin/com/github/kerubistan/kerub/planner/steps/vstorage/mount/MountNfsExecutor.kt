package com.github.kerubistan.kerub.planner.steps.vstorage.mount

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.utils.junix.mount.Mount
import org.apache.sshd.client.session.ClientSession

class MountNfsExecutor(override val hostCommandExecutor: HostCommandExecutor,
					   override val hostConfigurationDao: HostConfigurationDao) :
		AbstractMountExecutor<MountNfs>() {

	override fun performOnHost(session: ClientSession,
							   step: MountNfs) {
		Mount.mountNfs(session, step.remoteHost.address, step.remoteDirectory, step.directory)
	}

	override fun updateHostConfiguration(
			it: HostConfiguration, step: MountNfs) = it.copy()
}