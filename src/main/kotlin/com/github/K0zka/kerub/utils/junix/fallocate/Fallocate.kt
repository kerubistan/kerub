package com.github.K0zka.kerub.utils.junix.fallocate

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession

object Fallocate : OsCommand {
	override fun available(hostCapabilities: HostCapabilities?): Boolean
			= hostCapabilities?.os == OperatingSystem.Linux
			&& hostCapabilities?.installedSoftware.any { it.name == "util-linux"}

	fun dig(session : ClientSession, file : String) {
		session.executeOrDie("fallocate $file")
	}

}