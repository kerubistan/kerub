package com.github.kerubistan.kerub.utils.junix.ovs

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.csv.parseAsCsv
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.want
import io.github.kerubistan.kroki.strings.toUUID
import org.apache.sshd.client.session.ClientSession

object Vsctl : OsCommand {

	override fun available(hostCapabilities: HostCapabilities?) =
			hostCapabilities?.index?.installedPackageNames?.contains("openvswitch-switch") ?: false

	fun createBridge(session: ClientSession, bridgeName: String) {
		session.executeOrDie("ovs-vsctl add-br $bridgeName")
	}

	fun removeBridge(session: ClientSession, bridgeName: String) {
		session.executeOrDie("ovs-vsctl del-br $bridgeName")
	}

	fun createPort(session: ClientSession, bridgeName: String, portName: String, options: String? = null) {
		session.executeOrDie("ovs-vsctl add-port $bridgeName $portName" + (options ?: ""))
	}

	fun createGrePort(session: ClientSession, bridgeName: String, portName: String, remoteIp: String) {
		createPort(session, bridgeName, portName, " -- set Interface $portName type=gre options:remote_ip=$remoteIp")
	}

	fun createInternalPort(session: ClientSession, bridgeName: String, portName: String) {
		createPort(session, bridgeName, portName, " -- set Interface $portName type=internal")
	}

	fun removePort(session: ClientSession, bridgeName: String, portName: String) {
		session.executeOrDie("ovs-vsctl del-port $bridgeName $portName")
	}

	fun listBridges(session: ClientSession): List<OvsBridge> = parseAsCsv(session.executeOrDie("ovs-vsctl list br")).map {
		OvsBridge(
				id = it.want("_uuid").toUUID(),
				name = it.want("name")
		)
	}

	fun listPorts(session: ClientSession): List<OvsPort> = parseAsCsv(session.executeOrDie("ovs-vsctl list port")).map {
		OvsPort(
				id = it.want("_uuid").toUUID()
		)
	}
}