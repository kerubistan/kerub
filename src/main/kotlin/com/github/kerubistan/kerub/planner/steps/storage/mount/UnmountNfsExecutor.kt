package com.github.kerubistan.kerub.planner.steps.storage.mount

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.utils.junix.mount.Mount
import com.github.kerubistan.kerub.utils.normalizePath
import org.apache.sshd.client.session.ClientSession

class UnmountNfsExecutor(override val hostCommandExecutor: HostCommandExecutor,
						 override val hostConfigurationDao: HostConfigurationDao) :
		AbstractMountExecutor<UnmountNfs>() {
	override fun performOnHost(session: ClientSession, step: UnmountNfs) {
		Mount.unmount(session, step.mountDir)
	}

	override fun updateHostConfiguration(hostConfiguration: HostConfiguration, step: UnmountNfs): HostConfiguration =
			hostConfiguration.copy(
					services = hostConfiguration.services.filterNot {
						it is NfsMount && it.localDirectory == step.mountDir.normalizePath()
					}
			)
}