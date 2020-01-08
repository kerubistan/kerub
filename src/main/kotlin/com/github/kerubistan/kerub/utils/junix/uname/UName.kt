package com.github.kerubistan.kerub.utils.junix.uname

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession

object UName : OsCommand {
	// any unix-like OS should have an uname
	override fun available(hostCapabilities: HostCapabilities?): Boolean = true

	fun kernelName(session: ClientSession) = session.executeOrDie("uname -s").trim()

	fun machineType(session: ClientSession) = session.executeOrDie("uname -m").trim()

	fun processorType(session: ClientSession) = session.executeOrDie("uname -p").trim()

	fun kernelVersion(session: ClientSession) = session.executeOrDie("uname -r").trim()

	fun operatingSystem(session: ClientSession) = session.executeOrDie("uname -o").trim()
}