package com.github.kerubistan.kerub.utils.junix.ovs

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.utils.csv.parseAsCsv
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.toUUID
import com.github.kerubistan.kerub.utils.want
import org.apache.sshd.client.session.ClientSession

object Vsctl : OsCommand {

	fun listPorts(session: ClientSession): List<OvsPort>
			= parseAsCsv(session.executeOrDie("ovs-vsctl list port")).map {
		OvsPort(
				id = it.want("_uuid").toUUID()
		)
	}

	fun listBridges(session: ClientSession): List<OvsBridge>
			= parseAsCsv(session.executeOrDie("ovs-vsctl list br")).map {
		OvsBridge(
				id = it.want("_uuid").toUUID(),
				name = it.want("name")
		)
	}


}