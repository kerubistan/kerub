package com.github.K0zka.kerub.utils.junix.mount

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.skip
import com.github.K0zka.kerub.utils.substringBetween
import org.apache.sshd.client.session.ClientSession

object BsdMount : OsCommand {
	override fun available(hostCapabilities: HostCapabilities?): Boolean
			= hostCapabilities?.os == OperatingSystem.BSD && hostCapabilities?.distribution?.name == "FreeBSD"


	fun listMounts(session: ClientSession): List<FsMount> =
			session.executeOrDie("mount").lines().map {
				val options = it.substringBetween("(", ")").split(", ")
				FsMount(
						device = it.substringBefore(" on ").trim(),
						mountPoint = it.substringBetween(" on ", "(").trim(),
						options = options.skip(),
						type = options.first()
				)
			}

}