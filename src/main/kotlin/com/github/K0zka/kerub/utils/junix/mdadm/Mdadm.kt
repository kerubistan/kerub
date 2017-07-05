package com.github.K0zka.kerub.utils.junix.mdadm

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession

object Mdadm : OsCommand {
	fun stop(session : ClientSession, device : String) {
		session.executeOrDie("mdadm -S $device")
	}

	fun build(session: ClientSession, device: String, level : Int, devices : List<String>) {
		session.executeOrDie("mdadm -B $device --level=$level ${devices.joinToString(separator = " ")}")
	}

}