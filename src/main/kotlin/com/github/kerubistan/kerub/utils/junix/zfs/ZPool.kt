package com.github.kerubistan.kerub.utils.junix.zfs

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.utils.skip
import com.github.kerubistan.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession

object ZPool {

	private val spaces = "\\s+".toRegex()

	fun list(session: ClientSession): List<ZfsPool> =
			session.executeOrDie("zpool list -oname,readonly,allocated,capacity,dedupratio,guid")
					.lines()
					.skip()
					.filter { it.isNotBlank() }
					.map {
						line ->
						val fields = line.split(spaces)
						ZfsPool(
								name = fields[0],
								readOnly = fields[1] == "on",
								allocated = fields[2].toSize(),
								capacity = fields[3].substringBefore("%").toDouble(),
								dedupratio = fields[4].substringBefore("x").toDouble(),
								id = fields[5]
						)
					}

}