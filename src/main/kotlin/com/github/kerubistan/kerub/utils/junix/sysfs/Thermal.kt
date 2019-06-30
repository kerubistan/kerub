package com.github.kerubistan.kerub.utils.junix.sysfs

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession

object Thermal : OsCommand {
	override fun available(hostCapabilities: HostCapabilities?) = hostCapabilities?.os == OperatingSystem.Linux

	fun getCpuTemperature(session: ClientSession) {
		session.executeOrDie("cat /")
	}

}