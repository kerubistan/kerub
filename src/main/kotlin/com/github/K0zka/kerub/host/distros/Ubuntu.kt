package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.execute
import org.apache.sshd.ClientSession
import com.github.K0zka.kerub.utils.version.Version
import com.github.K0zka.kerub.utils.SoftwarePackage

public class Ubuntu() : LsbDistribution("Ubuntu") {

	override fun listPackages(session: ClientSession): List<SoftwarePackage> {
		return session.execute(
				"dpkg-query -W --showformat \"$\\{Package\\}\t$\\{Version\\}\"")
				.trim()
				.split('\n').map {
			val split = it.split('\t')
			if (split.size() != 2) {
				throw IllegalArgumentException("Does not match expected input from dpkg-query: ${it}")
			}
			SoftwarePackage(split[0], Version.fromVersionString(split[1]))
		}
	}

	override fun handlesVersion(version: Version): Boolean {
		return version.major in "12".."14"
	}

	override fun installPackage(pack: String, session: ClientSession) {
		session.execute("apt-get -y install ${pack}")
	}

	override fun uninstallPackage(pack: String, session: ClientSession) {
		session.execute("apt-get -y remove ${pack}")
	}

}