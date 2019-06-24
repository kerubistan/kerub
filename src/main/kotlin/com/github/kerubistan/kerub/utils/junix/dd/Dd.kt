package com.github.kerubistan.kerub.utils.junix.dd

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession

object Dd : OsCommand {
	// dd should be part of the base install on any operating system (how about cygwin?)
	override fun available(hostCapabilities: HostCapabilities?): Boolean = true

	fun copy(session: ClientSession, source: String, target: String) {
		session.executeOrDie("dd if=$source of=$target")
	}

}