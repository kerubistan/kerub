package com.github.K0zka.kerub.host.fw

import com.github.K0zka.kerub.host.FireWall
import org.apache.sshd.client.session.ClientSession

class IptablesFireWall(private val session : ClientSession) : FireWall {
	override fun open(port: Int, proto: String) {
		throw UnsupportedOperationException()
	}

	override fun close(port: Int, proto: String) {
		throw UnsupportedOperationException()
	}
}