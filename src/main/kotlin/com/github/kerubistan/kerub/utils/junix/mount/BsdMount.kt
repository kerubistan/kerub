package com.github.kerubistan.kerub.utils.junix.mount

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.utils.junix.common.FreeBSD
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.skip
import io.github.kerubistan.kroki.strings.substringBetween
import org.apache.sshd.client.session.ClientSession

object BsdMount : OsCommand {
	override fun available(hostCapabilities: HostCapabilities?): Boolean
			= hostCapabilities?.os == OperatingSystem.BSD && hostCapabilities.distribution?.name == FreeBSD

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