package com.github.kerubistan.kerub.utils.junix.mount

import com.github.kerubistan.kerub.host.appendToFile
import org.apache.sshd.client.session.ClientSession

object FSTab {

	private const val fstabPath = "/etc/fstab"

	fun list(session: ClientSession) {

	}

	fun add(session: ClientSession) {
		session.appendToFile(fstabPath, "")
	}
}