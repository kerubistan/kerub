package com.github.kerubistan.kerub.planner.steps.storage.mount

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.HostService
import com.github.kerubistan.kerub.model.services.NfsMount

@JsonTypeName("unmount-nfs")
data class UnmountNfs(override val host: Host, val mountDir: String) : AbstractNfsMount() {

	override fun updateHostConfiguration(
			config: HostConfiguration): List<HostService> {
		return config.services.filterNot {
			it is NfsMount && it.localDirectory == mountDir
		}
	}

}
