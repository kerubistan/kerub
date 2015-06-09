package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.checkFileExists
import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.host.getFileContents
import com.github.K0zka.kerub.utils.SoftwarePackage
import com.github.K0zka.kerub.utils.junix.rpm.RpmListPackages
import com.github.K0zka.kerub.utils.version.Version
import org.apache.sshd.ClientSession

public class Centos6 : Distribution {
	override fun getVersion(session: ClientSession): Version =
		Version.fromVersionString(
				session.getFileContents("/etc/redhat-release").substringAfter("CentOS release").replace("(Final)".toRegex(), "")
		                         )

	override fun name(): String {
		return "Centos"
	}

	override fun handlesVersion(version: Version): Boolean {
		return version.major == "6"
	}

	override fun detect(session: ClientSession): Boolean =
		session.checkFileExists("/etc/redhat-release") &&
		session.getFileContents("/etc/redhat-release").startsWith("CentOS release 6")

	override fun installPackage(pack: String, session: ClientSession) {
		session.execute("yum -y install ${pack}")
	}

	override fun uninstallPackage(pack: String, session: ClientSession) {
		session.execute("yum -y remove ${pack}")
	}

	override fun listPackages(session: ClientSession): List<SoftwarePackage> =
		RpmListPackages.execute(session)
}