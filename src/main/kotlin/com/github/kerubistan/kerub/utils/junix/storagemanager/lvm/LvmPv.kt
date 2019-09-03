package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

import com.github.kerubistan.kerub.host.bashMonitor
import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.utils.junix.common.MonitorOutputStream
import com.github.kerubistan.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession

/**
 * Utility to handle LVM Physical Volumes
 */
object LvmPv : Lvm() {

	private const val listPvs = "lvm pvs -o pv_uuid,pv_name,pv_size,pv_free,vg_uuid,vg_name $listOptions"

	fun list(session: ClientSession): List<PhysicalVolume> = toPvList(session.executeOrDie(listPvs))

	private fun toPvList(output: String): List<PhysicalVolume> =
			output.lines()
					.filterNot { it.isBlank() }
					.map { row ->
						val fields = row.trim().split(fieldSeparator)
						PhysicalVolume(
								device = fields[1],
								size = fields[2].toSize(),
								freeSize = fields[3].toSize(),
								volumeGroupId = fields[4],
								volumeGroupName = fields[5]
						)
					}

	fun monitor(session: ClientSession, callback: (List<PhysicalVolume>) -> Unit) {
		session.bashMonitor(listPvs, 60, separator, MonitorOutputStream(separator, callback, ::toPvList))
	}

	fun move(session: ClientSession, pv: String) {
		session.executeOrDie("lvm pvmove $pv")
	}
}