package com.github.kerubistan.kerub.utils.junix.iostat

import com.github.kerubistan.kerub.host.process
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.common.Ubuntu
import org.apache.sshd.client.session.ClientSession

object IOStat : OsCommand {

	override fun providedBy(): List<Pair<(SoftwarePackage) -> Boolean, List<String>>> = listOf(
			{ pack: SoftwarePackage -> pack.name == Ubuntu } to listOf("sysstat")
	)

	fun monitor(session: ClientSession, listener: (List<IOStatEvent>) -> Unit) {
		session.process("iostat  -j ID -x -d 1", output = IOStatOutputStream(listener))
	}

}