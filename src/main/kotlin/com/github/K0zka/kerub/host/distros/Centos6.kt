package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.host.checkFileExists
import com.github.K0zka.kerub.host.getFileContents
import com.github.K0zka.kerub.host.packman.YumPackageManager
import com.github.K0zka.kerub.model.Version
import org.apache.sshd.ClientSession

public class  Centos6 : AbstractLinux() {
	override fun getPackageManager(session: ClientSession): PackageManager
			= YumPackageManager(session)

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

}