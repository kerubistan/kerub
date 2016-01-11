package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.host.packman.YumPackageManager
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.junix.packagemanager.rpm.RpmListPackages
import com.github.K0zka.kerub.utils.junix.packagemanager.yum.Yum
import org.apache.sshd.ClientSession

public class Centos7 : LsbDistribution("Centos Linux") {
	override fun getPackageManager(session: ClientSession): PackageManager
			= YumPackageManager(session)

	override fun handlesVersion(version: Version): Boolean {
		return version.major == "7"
	}

}