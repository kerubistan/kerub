package com.github.K0zka.kerub.utils.junix.service.systemd

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession

object SystemCtl : OsCommand {

	fun enable(client: ClientSession, serviceName: String) {
		client.executeOrDie("systemctl enable $serviceName")
	}

	fun disable(client: ClientSession, serviceName: String) {
		client.executeOrDie("systemctl disable $serviceName")
	}

	fun start(client: ClientSession, serviceName: String) {
		client.executeOrDie("systemctl start $serviceName")
	}

	fun stop(client: ClientSession, serviceName: String) {
		client.executeOrDie("systemctl stop $serviceName")
	}

}