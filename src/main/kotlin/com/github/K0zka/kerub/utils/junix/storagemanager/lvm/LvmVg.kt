package com.github.K0zka.kerub.utils.junix.storagemanager.lvm

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.utils.toSize
import org.apache.commons.io.input.NullInputStream
import org.apache.commons.io.output.NullOutputStream
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream

object LvmVg : Lvm() {

	class LvmVgMonitorOutputStream(
			private val callback: (List<VolumeGroup>) -> Unit
	) : OutputStream() {
		private val buffer = StringBuilder()
		private var vgs = listOf<VolumeGroup>()

		override fun write(data: Int) {
			if (data == 10) {
				val row = buffer.toString()
				if (row.trim() == separator) {
					callback(vgs)
					vgs = listOf()
				} else {
					vgs += parseRow(row)
				}
				buffer.setLength(0)
			} else {
				buffer.append(data.toChar())
			}
		}
	}

	fun monitor(session: ClientSession, callback: (List<VolumeGroup>) -> Unit) {
		val channel = session.createExecChannel(
				"""bash -c "while true; do lvm vgs -o $vgFields $listOptions; echo $separator; sleep 60; done" """)

		channel.`in` = NullInputStream(0)
		channel.err = NullOutputStream()
		channel.out = LvmVgMonitorOutputStream(callback)
		channel.open().verify()

	}

	fun list(session: ClientSession): List<VolumeGroup> =
			session.executeOrDie(
					"lvm vgs -o $vgFields $listOptions")
					.trim().lines().map {
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
}