package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.host.packman.RaspbianPackageManager
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.model.lom.PowerManagementInfo
import com.github.K0zka.kerub.utils.silent
import org.apache.sshd.client.session.ClientSession

class UbuntuBSD : AbstractDebian("ubuntuBSD") {
	override fun handlesVersion(version: Version): Boolean =
			silent { version.major.toInt() >= 16 } ?: false

	// TODO https://github.com/kerubistan/kerub/issues/170
	override fun detectPowerManagement(session: ClientSession): List<PowerManagementInfo> = listOf()

	//the output format of dpkg is also not conventional ubuntu
	override fun getPackageManager(session: ClientSession): PackageManager
			= RaspbianPackageManager(session)

}