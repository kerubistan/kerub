package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.host.packman.PkgPackageManager
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession

/**
 * FreeBSD distribution.
 * Some settings need to be modified in sshd configuration:
 * PermitRootLogin yes
 */
class FreeBSD : Distribution {
	override fun installMonitorPackages(session: ClientSession) {
		//TODO issue #57
	}

	override fun getRequiredPackages(utility: OsCommand): List<String> {
		//TODO issue #57
		return listOf()
	}

	override val operatingSystem = OperatingSystem.BSD

	override fun getVersion(session: ClientSession): Version = Version.fromVersionString(session.execute("uname -s"))

	override fun name(): String = "FreeBSD"

	override fun handlesVersion(version: Version): Boolean = version.major >= "10"

	override fun detect(session: ClientSession): Boolean = session.execute("uname -s") == "FreeBSD"

	override fun getPackageManager(session: ClientSession) = PkgPackageManager(session)

	override fun startMonitorProcesses(session: ClientSession, host: Host, hostDynDao: HostDynamicDao) {
		//TODO issue #57
	}
}