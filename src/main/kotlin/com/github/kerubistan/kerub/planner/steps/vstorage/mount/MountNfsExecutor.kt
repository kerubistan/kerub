package com.github.kerubistan.kerub.planner.steps.vstorage.mount

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.utils.junix.mkdir.MkDir
import com.github.kerubistan.kerub.utils.junix.mount.Mount
import org.apache.sshd.client.session.ClientSession

class MountNfsExecutor(override val hostCommandExecutor: HostCommandExecutor,
					   override val hostConfigurationDao: HostConfigurationDao) :
		AbstractMountExecutor<MountNfs>() {

	override fun performOnHost(session: ClientSession,
							   step: MountNfs) {
		MkDir.mkdir(session, step.directory)
		Mount.mountNfs(session, step.remoteHost.address, step.remoteDirectory, step.directory)
	}

	override fun updateHostConfiguration(
			hostConfiguration: HostConfiguration, step: MountNfs) = hostConfiguration.copy(
			services = hostConfiguration.services +
					NfsMount(
							remoteDirectory = step.remoteDirectory,
							localDirectory = step.directory,
							remoteHostId = step.remoteHost.id
					)
	)
}