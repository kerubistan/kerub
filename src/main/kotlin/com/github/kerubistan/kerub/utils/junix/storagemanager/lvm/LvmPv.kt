package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession

/**
 * Utility to handle LVM Physical Volumes
 */
object LvmPv : Lvm() {
	fun list(session: ClientSession): List<PhysicalVolume>
			= session.executeOrDie("lvm pvs -o pv_uuid,pv_name,pv_size,pv_free,vg_uuid $listOptions")
			.lines()
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
}