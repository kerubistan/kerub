package com.github.kerubistan.kerub.utils.junix.file

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession

object Mv : OsCommand {
	override fun available(hostCapabilities: HostCapabilities?) = true

	fun move(session: ClientSession, source : String, target : String) {
		session.executeOrDie("mv $source $target")
	}
}