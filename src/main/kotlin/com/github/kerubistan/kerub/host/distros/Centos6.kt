package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.host.PackageManager
import com.github.kerubistan.kerub.host.checkFileExists
import com.github.kerubistan.kerub.host.getFileContents
import com.github.kerubistan.kerub.host.packman.YumPackageManager
import com.github.kerubistan.kerub.model.Version
import org.apache.sshd.client.session.ClientSession

class Centos6 : AbstractLinux() {
	override fun getPackageManager(session: ClientSession): PackageManager
			= YumPackageManager(session)

	override fun getVersion(session: ClientSession): Version =
			Version.fromVersionString(
					session.getFileContents("/etc/redhat-release").substringAfter("CentOS release").replace("(Final)".toRegex(), "")
			)

	override fun name(): String = "CentOS Linux"

	override fun handlesVersion(version: Version): Boolean {
		return version.major == "6"
	}

	override fun detect(session: ClientSession): Boolean =
			session.checkFileExists("/etc/redhat-release") &&
					session.getFileContents("/etc/redhat-release").startsWith("CentOS release 6")

}