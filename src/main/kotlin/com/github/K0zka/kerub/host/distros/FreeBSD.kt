package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.junix.packagemanager.pkg.Pkg
import org.apache.sshd.ClientSession

/**
 * FreeBSD distribution.
 * Some settings need to be modified in sshd configuration:
 * PermitRootLogin yes
 */
public class FreeBSD : Distribution {

	override fun getRequiredPackages(utility: OsCommand): List<String> {
		//TODO!!!
		return listOf()
	}

	override val operatingSystem = OperatingSystem.BSD

	override fun getVersion(session: ClientSession): Version = Version.fromVersionString(session.execute("uname -s"))

	override fun name(): String = "FreeBSD"

	override fun handlesVersion(version: Version): Boolean = version.major >= "10"

	override fun detect(session: ClientSession): Boolean = session.execute("uname -s") == "FreeBSD"

	override fun installPackage(pack: String, session: ClientSession) {
		Pkg.installPackage(session, pack)
	}

	override fun uninstallPackage(pack: String, session: ClientSession) {
		Pkg.uninstallPackage(session, pack)
	}

	override fun listPackages(session: ClientSession): List<SoftwarePackage>
			= Pkg.listPackages(session)

	override fun startMonitorProcesses(session: ClientSession, host: Host, hostDynDao: HostDynamicDao) {
		//TODO
	}
}