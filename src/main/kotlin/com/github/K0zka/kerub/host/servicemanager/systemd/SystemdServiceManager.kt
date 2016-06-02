package com.github.K0zka.kerub.host.servicemanager.systemd

import com.github.K0zka.kerub.host.ServiceManager
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.junix.iscsi.tgtd.TgtAdmin
import com.github.K0zka.kerub.utils.junix.service.systemd.SystemCtl
import com.github.K0zka.kerub.utils.junix.virt.virsh.Virsh
import org.apache.sshd.client.session.ClientSession

class SystemdServiceManager(private val client: ClientSession) : ServiceManager {

	private val services = mapOf<OsCommand, String>(
			TgtAdmin to "tgtd",
			Virsh to "libvirtd"
	)

	private fun serviceName(service: OsCommand) = requireNotNull(services[service]) { "service not known: $service" }

	override fun start(service: OsCommand) {
		SystemCtl.start(client, serviceName(service))
	}

	override fun stop(service: OsCommand) {
		SystemCtl.stop(client, serviceName(service))
	}

	override fun enable(service: OsCommand) {
		SystemCtl.enable(client, serviceName(service))
	}

	override fun disable(service: OsCommand) {
		SystemCtl.disable(client, serviceName(service))
	}
}