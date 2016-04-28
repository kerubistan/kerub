package com.github.K0zka.kerub.host.fw

import com.github.K0zka.kerub.host.FireWall
import com.github.K0zka.kerub.utils.junix.fw.iptables.IpTables
import org.apache.sshd.client.session.ClientSession

class IptablesFireWall(private val session : ClientSession) : FireWall {
	override fun open(port: Int, proto: String) {
		IpTables.open(session, port, proto)
	}

	override fun close(port: Int, proto: String) {
		IpTables.close(session, port, proto)
	}
}