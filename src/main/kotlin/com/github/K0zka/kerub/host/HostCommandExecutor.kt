package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.model.Host
import org.apache.sshd.ClientSession

interface HostCommandExecutor {
	fun execute(host: Host, closure: (ClientSession) -> Unit)
}