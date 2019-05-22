package com.github.kerubistan.kerub.utils.junix.packagemanager.dpkg

import com.github.kerubistan.kerub.host.execute
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import org.apache.sshd.client.session.ClientSession

object Dpkg {

	fun listRasbianPackages(session: ClientSession): List<SoftwarePackage> {
		return session.execute(
				"dpkg-query -W")
				.trim()
				.lines().map {
			parseDpkgOutputLine(it)
		}
	}

	fun listPackages(session: ClientSession): List<SoftwarePackage> {
		return session.execute(
				"dpkg-query -W --showformat '\${Package}\\t\${Version}\\n'")
				.trim()
				.lines().map {
			parseDpkgOutputLine(it)
		}
	}

	private fun parseDpkgOutputLine(it: String): SoftwarePackage {
		val split = it.split('\t')
		if (split.size != 2) {
			throw IllegalArgumentException("Does not match expected input from dpkg-query: $it")
		}
		return SoftwarePackage(split[0], Version.fromVersionString(split[1]))
	}


}