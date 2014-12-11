package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.host
import org.apache.sshd.ClientSession
import com.github.K0zka.kerub.utils.version.Version
import com.github.K0zka.kerub.utils.SoftwarePackage

public class Fedora : LsbDistribution("Fedora") {

	override fun listPackages(session: ClientSession): List<SoftwarePackage> {
		return session.execute("rpm -qa --queryformat \"%{NAME}\\t%{VERSION}\\n\"")
				.trim()
				.split("\n")
				.map {
					SoftwarePackage(it.substringBefore("\t"), Version.fromVersionString(it.substringAfter("\t")))
				}
	}

	override fun handlesVersion(version: Version): Boolean {
		return version.major in "19".."21"
	}

	override fun installPackage(pack: String, session: ClientSession) {
		session.execute("yum -y install ${pack}")
	}

	override fun uninstallPackage(pack: String, session: ClientSession) {
		session.execute("yum -y remove ${pack}")
	}
}