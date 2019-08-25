package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

import com.github.kerubistan.kerub.host.bashMonitor
import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream

/**
 * Utility to handle LVM Physical Volumes
 */
object LvmPv : Lvm() {

	private const val listPvs = "lvm pvs -o pv_uuid,pv_name,pv_size,pv_free,vg_uuid $listOptions"

	fun list(session: ClientSession): List<PhysicalVolume>
			= session.executeOrDie(listPvs)
			.toPvList()

	fun String.toPvList() : List<PhysicalVolume> =
			this.lines()
			.filterNot { it.isBlank() }
			.map {
				row ->
				val fields = row.trim().split(fieldSeparator)
				PhysicalVolume(
						device = fields[1],
						size = fields[2].toSize(),
						freeSize = fields[3].toSize(),
						volumeGroupId = fields[4]
				)
			}

	class LvmPvMonitorOutputStream(private val callback : (List<PhysicalVolume>) -> Unit) : OutputStream() {

		private val buff = StringBuilder()

		override fun write(data: Int) {
			buff.append(data.toChar())
			if(buff.endsWith(separator)) {
				callback(buff.removeSuffix(separator).toString().toPvList())
				buff.clear()
			}
		}

	}

	fun monitor(session: ClientSession, callback : (List<PhysicalVolume>) -> Unit) {
		session.bashMonitor(listPvs, 60, separator, LvmPvMonitorOutputStream(callback))
	}

	fun move(session: ClientSession, pv : String) {
		session.executeOrDie("lvm pvmove $pv")
	}
}