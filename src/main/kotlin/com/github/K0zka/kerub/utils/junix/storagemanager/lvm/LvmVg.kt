package com.github.K0zka.kerub.utils.junix.storagemanager.lvm

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.utils.toSize
import org.apache.sshd.ClientSession

object LvmVg {

	fun list(session: ClientSession): List<VolumeGroup> =
			session.executeOrDie(
					"vgs -o vg_uuid,vg_name,vg_size,vg_free,vg_extent_count,vg_free_count ${listOptions}")
					.trim().split("\n").map {
				row ->
				val fields = row.trim().split(",")
				VolumeGroup(
						id = fields[0],
						name = fields[1],
						size = fields[2].toSize(),
						freeSize = fields[3].toSize(),
						pes = fields[4].toLong(),
						freePes = fields[5].toLong()
				)
			}
}