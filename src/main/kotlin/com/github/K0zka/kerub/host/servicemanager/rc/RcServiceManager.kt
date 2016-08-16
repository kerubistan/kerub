package com.github.K0zka.kerub.host.servicemanager.rc

import com.github.K0zka.kerub.host.ServiceManager
import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.junix.iscsi.ctld.Ctld
import org.apache.sshd.client.session.ClientSession

class RcServiceManager(private val session: ClientSession) : ServiceManager {

	val serviceMap = mapOf<OsCommand, String>(
			Ctld to "ctld"
	)

	override fun start(service: OsCommand) {
		session.executeOrDie("service start ${serviceName(service)}")
	}

	override fun stop(service: OsCommand) {
		session.executeOrDie("service stop ${serviceName(service)}")
	}

	override fun enable(service: OsCommand) {
		session.executeOrDie("echo ${serviceName(service)}_enable=\\\"YES\\\" >> /etc/rc.conf")
	}

	private fun serviceName(service: OsCommand) = requireNotNull(serviceMap[service]) { "service $service not registered" }

	override fun disable(service: OsCommand) {
		session.executeOrDie(
				"""bash -c  " grep -v ${serviceName(service)}_enable /etc/rc.conf > /etc/rc.conf.upd && mv /etc/rc.conf.upd /etc/rc.conf " """
		)
	}
}