package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import org.apache.sshd.ClientSession

public abstract class AbstractDebian(distroName: String) : LsbDistribution(distroName) {

	override fun listPackages(session: ClientSession): List<SoftwarePackage> {
		return session.execute(
				"dpkg-query -W --showformat \"$\\{Package\\}\t$\\{Version\\}\"")
				.trim()
				.split('\n').map {
			parseDpkgOutputLine(it)
		}
	}

	fun parseDpkgOutputLine(it: String): SoftwarePackage {
		val split = it.split('\t')
		if (split.size != 2) {
			throw IllegalArgumentException("Does not match expected input from dpkg-query: ${it}")
		}
		return SoftwarePackage(split[0], Version.fromVersionString(split[1]))
	}

	override fun installPackage(pack: String, session: ClientSession) {
		session.execute("apt-get -y install ${pack}")
	}

	override fun uninstallPackage(pack: String, session: ClientSession) {
		session.execute("apt-get -y remove ${pack}")
	}


}