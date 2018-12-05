package com.github.kerubistan.kerub.utils.junix.file

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession

object Rm : OsCommand {

	override fun available(hostCapabilities: HostCapabilities?) = true

	fun remove(session: ClientSession, path: String) {
		session.executeOrDie("rm -f $path")
	}
}