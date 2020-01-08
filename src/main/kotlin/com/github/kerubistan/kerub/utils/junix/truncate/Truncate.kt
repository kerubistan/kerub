package com.github.kerubistan.kerub.utils.junix.truncate

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

object Truncate : OsCommand {
	override fun available(hostCapabilities: HostCapabilities?): Boolean = true

	fun truncate(session: ClientSession, path: String, virtualSize: BigInteger) {
		session.executeOrDie("truncate -s $virtualSize $path")
	}

}