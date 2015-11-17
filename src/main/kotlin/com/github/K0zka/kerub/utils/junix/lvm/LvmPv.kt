package com.github.K0zka.kerub.utils.junix.lvm

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.utils.toSize
import org.apache.sshd.ClientSession

/**
 * Utility to handle LVM Physical Volumes
 */
object LvmPv {
	fun list(session: ClientSession): List<PhysicalVolume>
			= session.executeOrDie("pvs -o pv_uuid,pv_name,pv_size,pv_free,vg_uuid ${listOptions}").trim().split("\n").map {
		row ->
		val fields = row.trim().split(fieldSeparator)
		PhysicalVolume(
				device = fields[1],
				size = fields[2].toSize(),
				freeSize = fields[3].toSize(),
				volumeGroupId = fields[4]
		)
	}
}