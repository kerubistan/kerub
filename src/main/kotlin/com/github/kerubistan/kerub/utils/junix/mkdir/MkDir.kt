package com.github.kerubistan.kerub.utils.junix.mkdir

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession

object MkDir : OsCommand{
	override fun available(hostCapabilities: HostCapabilities?) = true

	fun mkdir(session: ClientSession, dir : String) {
		session.executeOrDie("mkdir -p $dir")
	}

}