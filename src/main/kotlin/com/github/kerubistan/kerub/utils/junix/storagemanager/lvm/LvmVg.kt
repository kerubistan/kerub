package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

import com.github.kerubistan.kerub.host.bashMonitor
import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.utils.junix.common.MonitorOutputStream
import com.github.kerubistan.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession

object LvmVg : Lvm() {

	private const val listVgs = "lvm vgs -o $vgFields $listOptions"

	fun monitor(session: ClientSession, callback: (List<VolumeGroup>) -> Unit) {
		session.bashMonitor("lvm vgs -o $vgFields $listOptions", 60, separator,
				MonitorOutputStream(separator, callback, ::parseOutput)
		)
	}

	fun list(session: ClientSession, vgName : String? = null): List<VolumeGroup> =
			parseOutput(session.executeOrDie(
					"lvm vgs ${vgName ?: ""} -o $vgFields $listOptions"))

	private fun parseOutput(output : String) = output.trim().lines().filterNot { it.isEmpty() }.map {
		row ->
		parseRow(row)
	}


	private fun parseRow(row: String): VolumeGroup {
		val fields = row.trim().split(fieldSeparator)
		return VolumeGroup(
				id = fields[0],
				name = fields[1],
				size = fields[2].toSize(),
				freeSize = fields[3].toSize(),
				pes = fields[4].toLong(),
				freePes = fields[5].toLong()
		)
	}

	fun reduce(session: ClientSession, vgName: String, pv : String) {
		session.executeOrDie("lvm vgreduce $vgName $pv")
	}

}