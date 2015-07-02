package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.SoftwarePackage
import org.apache.sshd.ClientSession

/**
 * FreeBSD distribution.
 * Some settings need to be modified in sshd configuration:
 * PermitRootLogin yes
 */
public class FreeBSD : Distribution {
	override fun getVersion(session: ClientSession): Version = Version.fromVersionString(session.execute("uname -s"))

	override fun name(): String = "FreeBSD"

	override fun handlesVersion(version: Version): Boolean = version.major >= "10"

	override fun detect(session: ClientSession): Boolean = session.execute("uname -s") == "FreeBSD"

	override fun installPackage(pack: String, session: ClientSession) {
		session.execute("""pkg install -y ${pack} """)
	}

	override fun uninstallPackage(pack: String, session: ClientSession) {
		session.execute("""pkg remove -y ${pack} """)
	}

	override fun listPackages(session: ClientSession): List<SoftwarePackage>
			= session.execute("""pkg query "%n\t%v" """).split('\n').toList().map {
		val split = it.split('\t')
		SoftwarePackage(split[0], Version.fromVersionString(split[1]))
	}

	override fun startMonitorProcesses(session: ClientSession, host: Host, hostDynDao: HostDynamicDao) {
		//TODO
	}
}